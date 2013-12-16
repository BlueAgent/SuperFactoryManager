package vswe.stevesjam.components;


public enum ConnectionSet {
    STANDARD("Standard", ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    CONTINUOUSLY("On interval", ConnectionOption.INTERVAL),
    REDSTONE("Redstone controlled", ConnectionOption.REDSTONE_PULSE_HIGH, ConnectionOption.REDSTONE_HIGH, ConnectionOption.REDSTONE_LOW, ConnectionOption.REDSTONE_PULSE_LOW),
    STANDARD_CONDITION("Condition", ConnectionOption.STANDARD_INPUT, ConnectionOption.CONDITION_TRUE, ConnectionOption.CONDITION_FALSE);


    private ConnectionOption[] connections;
    private int outputCount;
    private int inputCount;
    private String name;


    private ConnectionSet(String name, ConnectionOption... connections) {
        this.connections = connections;

        for (ConnectionOption connection : connections) {
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
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

    @Override
    public String toString() {
        return name;
    }
}
