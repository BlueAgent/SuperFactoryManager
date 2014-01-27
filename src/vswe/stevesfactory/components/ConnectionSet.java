package vswe.stevesfactory.components;


public enum ConnectionSet {
    STANDARD("Standard", ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    CONTINUOUSLY("On interval", ConnectionOption.INTERVAL),
    REDSTONE("Redstone controlled", ConnectionOption.REDSTONE_PULSE_HIGH, ConnectionOption.REDSTONE_HIGH, ConnectionOption.REDSTONE_LOW, ConnectionOption.REDSTONE_PULSE_LOW),
    STANDARD_CONDITION("Condition", ConnectionOption.STANDARD_INPUT, ConnectionOption.CONDITION_TRUE, ConnectionOption.CONDITION_FALSE),
    MULTIPLE_INPUT_2("Collector - 2 inputs",  ConnectionOption.STANDARD_INPUT,  ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    MULTIPLE_INPUT_5("Collector - 5 inputs",  ConnectionOption.STANDARD_INPUT,  ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    MULTIPLE_OUTPUT_2("Split - 2 outputs",  ConnectionOption.STANDARD_INPUT,  ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT),
    MULTIPLE_OUTPUT_5("Split - 5 outputs",  ConnectionOption.STANDARD_INPUT,  ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT),
    EMPTY("Declaration"),
    FOR_EACH("For each", ConnectionOption.STANDARD_INPUT, ConnectionOption.FOR_EACH, ConnectionOption.STANDARD_OUTPUT),
    BUD("Block update detector", ConnectionOption.BUD, ConnectionOption.BUD_PULSE, ConnectionOption.BUD_MATCH);


    private ConnectionOption[] connections;
    private int outputCount;
    private int inputCount;
    private int sideCount;
    private String name;


    private ConnectionSet(String name, ConnectionOption... connections) {
        this.connections = connections;

        for (ConnectionOption connection : connections) {
            if (connection.isInput()) {
                inputCount++;
            }else if(connection.getType() == ConnectionOption.ConnectionType.OUTPUT) {
                outputCount++;
            }else{
                sideCount++;
            }
        }

        this.name = name;
    }


    public ConnectionOption[] getConnections() {
        return connections;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getSideCount() {
        return sideCount;
    }

    @Override
    public String toString() {
        return name;
    }
}
