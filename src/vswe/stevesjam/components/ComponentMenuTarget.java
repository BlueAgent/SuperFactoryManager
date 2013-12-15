package vswe.stevesjam.components;


import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.ContainerJam;
import vswe.stevesjam.interfaces.GuiJam;
import vswe.stevesjam.network.DataBitHelper;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;
import vswe.stevesjam.network.PacketHandler;

public class ComponentMenuTarget extends ComponentMenu {


    public ComponentMenuTarget(FlowComponent parent) {
        super(parent);

        selectedDirectionId = -1;
        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(startTextBox = new TextBoxNumber(39 ,49, 2, false) {
            @Override
            public void onNumberChanged() {
                if (selectedDirectionId != -1 && getParent().getJam().worldObj.isRemote) {
                    writeData(DataTypeHeader.START, getNumber());
                }
            }
        });
        textBoxes.addTextBox(endTextBox = new TextBoxNumber(60 ,49, 2, false) {
            @Override
            public void onNumberChanged() {
                if (selectedDirectionId != -1 && getParent().getJam().worldObj.isRemote) {
                    writeData(DataTypeHeader.END, getNumber());
                }
            }
        });
    }

    private static final int DIRECTION_SIZE_W = 31;
    private static final int DIRECTION_SIZE_H = 12;
    private static final int DIRECTION_SRC_X = 0;
    private static final int DIRECTION_SRC_Y = 70;
    private static final int DIRECTION_X_LEFT = 2;
    private static final int DIRECTION_X_RIGHT = 88;
    private static final int DIRECTION_Y = 5;
    private static final int DIRECTION_MARGIN = 10;
    private static final int DIRECTION_TEXT_X = 2;
    private static final int DIRECTION_TEXT_Y = 3;

    private static final int BUTTON_SIZE_W = 42;
    private static final int BUTTON_SIZE_H = 12;
    private static final int BUTTON_SRC_X = 0;
    private static final int BUTTON_SRC_Y = 106;
    private static final int BUTTON_X = 39;
    private static final int BUTTON_TEXT_Y = 5;



    private Button[] buttons = {new Button(5) {
        @Override
        protected String getLabel() {
            return isActive(selectedDirectionId) ? "Deactivate" : "Activate";
        }

        @Override
        protected String getMouseOverText() {
            return isActive(selectedDirectionId) ? "Click to prevent this side from being used" : "Click to use this side";
        }

        @Override
        protected void onClicked() {
            writeData(DataTypeHeader.ACTIVATE, isActive(selectedDirectionId) ? 0 : 1);
        }
    },
    new Button(27) {
        @Override
        protected String getLabel() {
            return useRange(selectedDirectionId) ? "Use all slots" : "Use id range";
        }

        @Override
        protected String getMouseOverText() {
            return useRange(selectedDirectionId) ? "Click to use all slots for this side instead" : "Click to use a slot id range for this specific side";
        }

        @Override
        protected void onClicked() {
            writeData(DataTypeHeader.USE_RANGE, useRange(selectedDirectionId) ? 0 : 1);
        }
    }};

    private TextBoxNumberList textBoxes;
    private TextBoxNumber startTextBox;
    private TextBoxNumber endTextBox;

    @Override
    public String getName() {
        return "Target";
    }


    public static ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;

    private int selectedDirectionId;
    private boolean[] activatedDirections = new boolean[directions.length];
    private boolean[] useRangeForDirections = new boolean[directions.length];
    private int[] startRange = new int[directions.length];
    private int[] endRange = new int[directions.length];



    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        for (int i = 0; i < directions.length; i++) {
            ForgeDirection direction = directions[i];

            int x = getDirectionX(i);
            int y = getDirectionY(i);

            int srcDirectionX = isActive(i) ? 1 : 0;
            int srcDirectionY = selectedDirectionId != -1 && selectedDirectionId != i ? 2 : GuiJam.inBounds(x, y, DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY) ? 1 : 0;


            gui.drawTexture(x, y, DIRECTION_SRC_X + srcDirectionX * DIRECTION_SIZE_W, DIRECTION_SRC_Y + srcDirectionY * DIRECTION_SIZE_H, DIRECTION_SIZE_W, DIRECTION_SIZE_H);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            int color =  selectedDirectionId != -1 && selectedDirectionId != i ? 0x70404040 : 0x404040;
            gui.drawString(direction.toString().charAt(0) + direction.toString().substring(1).toLowerCase(), x + DIRECTION_TEXT_X, y + DIRECTION_TEXT_Y, color);
            GL11.glPopMatrix();
        }

        if (selectedDirectionId != -1) {
            for (Button button : buttons) {
                int srcButtonY = GuiJam.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY) ? 1 : 0;

                gui.drawTexture(BUTTON_X, button.y, BUTTON_SRC_X, BUTTON_SRC_Y + srcButtonY * BUTTON_SIZE_H, BUTTON_SIZE_W, BUTTON_SIZE_H);
                gui.drawCenteredString(button.getLabel(), BUTTON_X, button.y + BUTTON_TEXT_Y, 0.5F, BUTTON_SIZE_W, 0x404040);
            }

            if (useRange(selectedDirectionId)) {
                textBoxes.draw(gui, mX, mY);
            }
        }
    }

    public boolean isActive(int i) {
        return activatedDirections[i];
    }

    private int getDirectionX(int i) {
        return i % 2 == 0 ? DIRECTION_X_LEFT : DIRECTION_X_RIGHT;
    }


    public boolean useRange(int i) {
        return useRangeForDirections[i];
    }

    public int getStart(int i) {
        return startRange[i];
    }

    public int getEnd(int i) {
        return endRange[i];
    }

    private void refreshTextBoxes() {
        if (selectedDirectionId != -1) {
            startTextBox.setNumber(startRange[selectedDirectionId]);
            endTextBox.setNumber(endRange[selectedDirectionId]);
        }
    }


    private int getDirectionY(int i) {
        return DIRECTION_Y + (DIRECTION_SIZE_H + DIRECTION_MARGIN) * (i / 2);
    }

    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {
        if (selectedDirectionId != -1) {
            for (Button button : buttons) {
                if (GuiJam.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)) {
                    gui.drawMouseOver(button.getMouseOverText(), mX, mY);
                }
            }
        }
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        for (int i = 0; i < directions.length; i++) {
            if (GuiJam.inBounds(getDirectionX(i), getDirectionY(i), DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY)) {
                if (selectedDirectionId == i) {
                    selectedDirectionId = -1;
                }else if (selectedDirectionId == -1) {
                    selectedDirectionId = i;
                    refreshTextBoxes();
                }

                break;
            }
        }

        if (selectedDirectionId != -1) {
            for (Button optionButton : buttons) {
                if (GuiJam.inBounds(BUTTON_X, optionButton.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)) {
                    optionButton.onClicked();
                    break;
                }
            }

            if (useRange(selectedDirectionId)) {
                textBoxes.onClick(mX, mY, button);
            }
        }
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {

    }

    private abstract class Button {
        private int y;

        protected Button(int y) {
            this.y = y;
        }

        protected abstract String getLabel();
        protected abstract String getMouseOverText();
        protected abstract void onClicked();
    }


    @Override
    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        if (selectedDirectionId != -1 && useRange(selectedDirectionId)) {
            return textBoxes.onKeyStroke(gui, c, k);
        }


        return false;
    }

    @Override
    public void writeData(DataWriter dw, TileEntityJam jam) {
        for (int i = 0; i < directions.length; i++) {
            dw.writeBoolean(isActive(i));
            dw.writeBoolean(useRange(i));
            if (useRange(i)) {
                dw.writeData(startRange[i], DataBitHelper.MENU_TARGET_RANGE);
                dw.writeData(endRange[i], DataBitHelper.MENU_TARGET_RANGE);
            }

        }
    }

    @Override
    public void readData(DataReader dr, TileEntityJam jam) {
        for (int i = 0; i < directions.length; i++) {

            activatedDirections[i] = dr.readBoolean();
            useRangeForDirections[i] = dr.readBoolean();
            if (useRange(i)) {
                startRange[i] = dr.readData(DataBitHelper.MENU_TARGET_RANGE);
                endRange[i] = dr.readData(DataBitHelper.MENU_TARGET_RANGE);
            }else{
                startRange[i] = 0;
                endRange[i] = 0;
            }


        }
    }

    @Override
    public void copyFrom(ComponentMenu menu) {
        ComponentMenuTarget menuTarget = (ComponentMenuTarget)menu;

        for (int i = 0; i < directions.length; i++) {
            activatedDirections[i] = menuTarget.activatedDirections[i];
            useRangeForDirections[i] = menuTarget.useRangeForDirections[i];
            startRange[i] = menuTarget.startRange[i];
            endRange[i] = menuTarget.endRange[i];
        }
    }

    @Override
    public void refreshData(ContainerJam container, ComponentMenu newData) {
        ComponentMenuTarget newDataTarget = (ComponentMenuTarget)newData;

        for (int i = 0; i < directions.length; i++) {
            if (activatedDirections[i] != newDataTarget.activatedDirections[i]) {
                activatedDirections[i] =  newDataTarget.activatedDirections[i];

                writeUpdatedData(container, i, DataTypeHeader.ACTIVATE, activatedDirections[i] ? 1 : 0);
            }

            if (useRangeForDirections[i] != newDataTarget.useRangeForDirections[i]) {
                useRangeForDirections[i] =  newDataTarget.useRangeForDirections[i];

                writeUpdatedData(container, i, DataTypeHeader.USE_RANGE, useRangeForDirections[i] ? 1 : 0);
            }

            if (startRange[i] != newDataTarget.startRange[i]) {
                startRange[i] =  newDataTarget.startRange[i];

                writeUpdatedData(container, i, DataTypeHeader.START, startRange[i]);
            }

            if (endRange[i] != newDataTarget.endRange[i]) {
                endRange[i] =  newDataTarget.endRange[i];

                writeUpdatedData(container, i, DataTypeHeader.END, endRange[i]);
            }
        }
    }

    private void writeUpdatedData(ContainerJam container, int id, DataTypeHeader header, int data) {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, id, header, data);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
       int direction = dr.readData(DataBitHelper.MENU_TARGET_DIRECTION_ID);
       int headerId = dr.readData(DataBitHelper.MENU_TARGET_TYPE_HEADER);
       DataTypeHeader header = getHeaderFromId(headerId);
       int data = dr.readData(header.bits);

       switch (header) {
           case ACTIVATE:
               activatedDirections[direction] = data != 0;
               break;
           case USE_RANGE:
               useRangeForDirections[direction] = data != 0;
               if (!useRange(direction)) {
                   startRange[direction] =  endRange[direction] = 0;
               }
               break;
           case START:
               startRange[direction] = data;
               refreshTextBoxes();
               break;
           case END:
               endRange[direction] = data;
               refreshTextBoxes();
       }
    }

    private void writeData(DataTypeHeader header, int data) {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, selectedDirectionId, header, data);
        PacketHandler.sendDataToServer(dw);
    }

    private void writeData(DataWriter dw, int id, DataTypeHeader header, int data) {
        dw.writeData(id, DataBitHelper.MENU_TARGET_DIRECTION_ID);
        dw.writeData(header.id, DataBitHelper.MENU_TARGET_TYPE_HEADER);
        dw.writeData(data, header.bits);
    }

    private enum DataTypeHeader {
        ACTIVATE(0, DataBitHelper.BOOLEAN),
        USE_RANGE(1, DataBitHelper.BOOLEAN),
        START(2, DataBitHelper.MENU_TARGET_RANGE),
        END(3, DataBitHelper.MENU_TARGET_RANGE);

        private int id;
        private DataBitHelper bits;

        private DataTypeHeader(int header, DataBitHelper bits) {
            this.id = header;
            this.bits = bits;
        }
    }

    private DataTypeHeader getHeaderFromId(int id) {
        for (DataTypeHeader header : DataTypeHeader.values()) {
            if (id == header.id) {
                return header;
            }
        }
        return  null;
    }
}
