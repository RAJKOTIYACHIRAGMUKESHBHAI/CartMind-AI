package com.cartmind.model;

public class ScoreBreakdown {

    private double requirementMatch;
    private double budgetFit;
    private double specificationMatch;
    private double qualitySignals;
    private double totalScore;

    public ScoreBreakdown() {
    }

    public ScoreBreakdown(
            double requirementMatch,
            double budgetFit,
            double specificationMatch,
            double qualitySignals,
            double totalScore
    ) {
        this.requirementMatch = requirementMatch;
        this.budgetFit = budgetFit;
        this.specificationMatch = specificationMatch;
        this.qualitySignals = qualitySignals;
        this.totalScore = totalScore;
    }

    public double getRequirementMatch() {
        return requirementMatch;
    }

    public void setRequirementMatch(double requirementMatch) {
        this.requirementMatch = requirementMatch;
    }

    public double getBudgetFit() {
        return budgetFit;
    }

    public void setBudgetFit(double budgetFit) {
        this.budgetFit = budgetFit;
    }

    public double getSpecificationMatch() {
        return specificationMatch;
    }

    public void setSpecificationMatch(double specificationMatch) {
        this.specificationMatch = specificationMatch;
    }

    public double getQualitySignals() {
        return qualitySignals;
    }

    public void setQualitySignals(double qualitySignals) {
        this.qualitySignals = qualitySignals;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}