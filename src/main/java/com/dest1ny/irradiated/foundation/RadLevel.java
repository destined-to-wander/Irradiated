package com.dest1ny.irradiated.foundation;

public class RadLevel {
    // Handles the radiation values for all the different rad types
    private final double[] levels;
    public RadLevel(double alpha, double beta, double gamma){
        levels = new double[]{
                Math.round(alpha * 100.0) / 100.0,
                Math.round(beta * 100.0) / 100.0,
                Math.round(gamma * 100.0) / 100.0
        };
    }

    public RadLevel(){
        levels = new double[]{0,0,0};
    }

    public double[] getValues(){
        return levels;
    }

    public void setValues(double alpha, double beta, double gamma){
        levels[0] = Math.round(alpha * 100.0) / 100.0;
        levels[1] = Math.round(beta * 100.0) / 100.0;
        levels[2] = Math.round(gamma * 100.0) / 100.0;
    }

    public void setValues(double radLevel){
        this.setValues(radLevel,radLevel,radLevel);
    }

    public void addValues(double alpha, double beta, double gamma){
        levels[0] = Math.round((levels[0] + alpha) * 100.0) / 100.0;
        levels[1] = Math.round((levels[0] + beta) * 100.0) / 100.0;
        levels[2] = Math.round((levels[0] + gamma) * 100.0) / 100.0;
    }

    public void addValues(double radLevel){
        this.addValues(radLevel,radLevel,radLevel);
    }

    public double getIonisingRad(){
        return levels[0] * 3 + levels[1] * 2 + levels[2];
    }

    public double getPenetratingRad(){
        return levels[2];
    }

    public double getTotalRad(){
        return levels[0] + levels[1] + levels[2];
    }

    public RadLevel multiply(double alpha, double beta, double gamma){
        levels[0] = Math.round((levels[0] * alpha) * 100.0) / 100.0;
        levels[1] = Math.round((levels[1] * beta) * 100.0) / 100.0;
        levels[2] = Math.round((levels[2] * gamma) * 100.0) / 100.0;
        return this;
    }

    public RadLevel multiply(double multiplier){
        return this.multiply(multiplier,multiplier,multiplier);
    }


    public RadLevel decay(){
        return this;
    }

    public double getAlpha(){
        return levels[0];
    }
    public double getBeta(){
        return levels[1];
    }
    public double getGamma(){
        return levels[2];
    }
}
