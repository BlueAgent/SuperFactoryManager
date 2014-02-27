package vswe.stevesfactory.settings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.CheckBox;
import vswe.stevesfactory.components.CheckBoxList;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.interfaces.IInterfaceRenderer;

import java.util.List;

@SideOnly(Side.CLIENT)
public class SettingsScreen implements IInterfaceRenderer {

    private TileEntityManager manager;

    public SettingsScreen(TileEntityManager manager) {
        this.manager = manager;
    }

    private static final int CHECK_BOX_WIDTH = 100;
    private static final int START_X = 10;
    private static final int START_SETTINGS_X = 380;
    private static final int MARGIN_X = 30;
    private static final int START_Y = 20;
    private static final int MAX_Y = 250;
    private abstract class CheckBoxSetting extends CheckBox {
        private CheckBoxSetting(Localization name) {
            super(name, getXAndGenerateY(name), currentY);

            setTextWidth(CHECK_BOX_WIDTH);
        }

        @Override
        public void onUpdate() {}
    }

    private int getXAndGenerateY(Localization name) {
        currentY += offsetY;

        String str = name.toString();

        List<String> lines = cachedGui.getLinesFromText(str, CHECK_BOX_WIDTH);
        int height = (int)((lines.size() + 1) * cachedGui.getFontHeight() * 0.7F);
        offsetY = height;

        if (currentY + height > MAX_Y) {
            currentY = START_Y;
            currentX += CHECK_BOX_WIDTH + MARGIN_X;
        }

        return currentX;
    }

    private CheckBoxList checkBoxes;
    private  String cachedString;
    private Localization localization = Localization.CLOSE_GROUP_LABEL;
    private int currentX;
    private int currentY;
    private int offsetY;
    private GuiManager cachedGui;
    private void addCheckboxes(GuiManager gui) {
        cachedGui = gui;
        cachedString = localization.toString();
        checkBoxes = new CheckBoxList();
        currentX = START_X;
        currentY = START_Y;
        offsetY = 0;
        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.CLOSE_GROUP_LABEL) {
            @Override
            public void setValue(boolean val) {
                Settings.setAutoCloseGroup(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isAutoCloseGroup();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.OPEN_MENU_LARGE_HIT_BOX) {
            @Override
            public void setValue(boolean val) {
                Settings.setLargeOpenHitBox(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isLargeOpenHitBox();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.OPEN_MENU_LARGE_HIT_BOX_MENU) {
            @Override
            public void setValue(boolean val) {
                Settings.setLargeOpenHitBoxMenu(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isLargeOpenHitBoxMenu();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.OPEN_GROUP_QUICK) {
            @Override
            public void setValue(boolean val) {
                Settings.setQuickGroupOpen(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isQuickGroupOpen();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.SHOW_COMMAND_TYPE) {
            @Override
            public void setValue(boolean val) {
                Settings.setCommandTypes(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isCommandTypes();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.AUTO_SIDE) {
            @Override
            public void setValue(boolean val) {
                Settings.setAutoSide(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isAutoSide();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.AUTO_BLACK_LIST) {
            @Override
            public void setValue(boolean val) {
                Settings.setAutoBlacklist(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isAutoBlacklist();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.ENLARGE_INTERFACES) {
            @Override
            public void setValue(boolean val) {
                Settings.setEnlargeInterfaces(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isEnlargeInterfaces();
            }
        });


        currentX = START_SETTINGS_X;
        currentY = START_Y;
        offsetY = 0;

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.LIMITLESS) {
            @Override
            public void setValue(boolean val) {
                Settings.setLimitless(manager, val);
            }

            @Override
            public boolean getValue() {
                return Settings.isLimitless(manager);
            }

            @Override
            public boolean isVisible() {
                return Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode;
            }
        });
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        if (cachedString == null || !localization.toString().equals(cachedString)) {
            addCheckboxes(gui);
        }

        gui.drawString(Localization.PREFERENCES.toString(), START_X - 2, 6, 0x404040);
        gui.drawString(Localization.SETTINGS.toString(), START_SETTINGS_X - 2, 6, 0x404040);
        checkBoxes.draw(gui, mX, mY);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int button) {
        checkBoxes.onClick(mX, mY);
        if (button == 1) {
            manager.specialRenderer = null;
        }
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onKeyTyped(GuiManager gui, char c, int k) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onScroll(int scroll) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
