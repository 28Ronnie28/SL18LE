package models.all;

import java.io.Serializable;

public class Result implements Serializable {

    private int resultTemplateID;
    private String studentNumber;
    private String resultName;
    private double result;
    private double resultMax;
    private double dpWeight;
    private double finalWeight;

    public Result(int resultTemplateID, String studentNumber, String resultName, double result, double resultMax, double dpWeight, double finalWeight) {
        this.resultTemplateID = resultTemplateID;
        this.studentNumber = studentNumber;
        this.resultName = resultName;
        this.result = result;
        this.resultMax = resultMax;
        this.dpWeight = dpWeight;
        this.finalWeight = finalWeight;
    }

    public int getResultTemplateID() {
        return resultTemplateID;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getResultName() {
        return resultName;
    }

    public double getResult() {
        return result;
    }

    public double getResultMax() {
        return resultMax;
    }

    public double getDpWeight() {
        return dpWeight;
    }

    public double getFinalWeight() {
        return finalWeight;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
