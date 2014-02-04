package vswe.stevesfactory.components;



import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.ArrayList;
import java.util.List;

public class Variable implements IContainerSelection {
    private int id;
    private FlowComponent declaration;
    private List<Integer> containers;
    private boolean executed;

    public Variable(int id) {
        this.id = id;
        containers = new ArrayList<Integer>();
    }

    public boolean isValid() {
        return declaration != null;
    }

    @Override
    public int getId() {
        return id;
    }

    private static final int VARIABLE_SRC_X = 32;
    private static final int VARIABLE_SRC_Y = 130;
    private static final int VARIABLE_SIZE = 14;

    @Override
    public void draw(GuiManager gui, int x, int y) {
        VariableColor.values()[id].applyColor();
        gui.drawTexture(x + 1, y + 1, VARIABLE_SRC_X, VARIABLE_SRC_Y, VARIABLE_SIZE, VARIABLE_SIZE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    @Override
    public String getDescription(GuiManager gui) {
        VariableColor color = VariableColor.values()[id];

        String name;
        if (getDeclaration() == null || getDeclaration().getComponentName() == null) {
            name = color.toString();
        }else{
            name = getDeclaration().getComponentName();
        }


        return color.getTextColor().toString() + name;
    }



    public void setDeclaration(FlowComponent flowComponent) {
        if (flowComponent == null || declaration == null) {
            declaration = flowComponent;
        }
    }

    public FlowComponent getDeclaration() {
        return declaration;
    }

    public List<Integer> getContainers() {
        return containers;
    }

    public void add(int id) {
        if (!containers.contains((Integer)id)) {
            containers.add(id);
        }
    }

    public void setContainers(List<Integer> containers) {
        this.containers = containers;
    }

    public boolean hasBeenExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public void clearContainers() {
        containers.clear();
    }

    public void remove(int id) {
        containers.remove((Integer)id);
    }

    private static final String NBT_EXECUTED = "Executed";
    private static final String NBT_SELECTION = "Selection";
    private static final String NBT_SELECTION_ID = "Id";

    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        executed = nbtTagCompound.getBoolean(NBT_EXECUTED);
        containers.clear();
        NBTTagList tagList = nbtTagCompound.getTagList(NBT_SELECTION);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound selectionTag = (NBTTagCompound)tagList.tagAt(i);

            containers.add((int)selectionTag.getShort(NBT_SELECTION_ID));
        }
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setBoolean(NBT_EXECUTED, executed);

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < containers.size(); i++) {
            NBTTagCompound selectionTag = new NBTTagCompound();

            selectionTag.setShort(NBT_SELECTION_ID, (short)(int)containers.get(i));
            tagList.appendTag(selectionTag);
        }

        nbtTagCompound.setTag(NBT_SELECTION, tagList);
    }
}
