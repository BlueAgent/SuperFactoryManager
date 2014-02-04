package vswe.stevesfactory.components;


import vswe.stevesfactory.Localization;

public class ComponentMenuTankCondition extends ComponentMenuTank {
    public ComponentMenuTankCondition(FlowComponent parent) {
        super(parent);
    }

    protected void initRadioButtons() {
        radioButtons.add(new RadioButtonInventory(0, Localization.RUN_SHARED_ONCE));
        radioButtons.add(new RadioButtonInventory(1, Localization.REQUIRE_ALL_TARGETS));
        radioButtons.add(new RadioButtonInventory(2, Localization.REQUIRE_ONE_TARGET));
    }
}
