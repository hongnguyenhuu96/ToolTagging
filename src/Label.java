/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Label is type of (symbol: name) example (prc: price)
 * @author hong
 */
public class Label {
    String name;
    String symbol;

    public Label(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
    /**
     * get name of the label
     * @return name of label
     */
    public String getName() {
        return name;
    }
    /**
     * set name for the label
     * @param name to set name of label
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get symbol of the label
     * @return symbol of label
     */
    public String getSymbol() {
        return symbol;
    }
    /**
     * set symbol for the label
     * @param symbol symbol of the label
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
}
