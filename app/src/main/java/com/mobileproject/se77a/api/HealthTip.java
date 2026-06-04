package com.mobileproject.se77a.api;

public class HealthTip {
    private String name;
    private String type;
    private String muscle;
    private String difficulty;
    private String instructions;
    private String equipment; // API Ninja renvoie un String, mais on peut s'adapter si c'est une liste
    private String safety_info;

    public HealthTip(String name, String type, String muscle, String difficulty, String instructions, String equipment, String safety_info) {
        this.name = name;
        this.type = type;
        this.muscle = muscle;
        this.difficulty = difficulty;
        this.instructions = instructions;
        this.equipment = equipment;
        this.safety_info = safety_info;
    }

    // Setters pour injecter la traduction
    public void setName(String name) { this.name = name; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setSafetyInfo(String safetyInfo) { this.safety_info = safetyInfo; }

    // Getters
    public String getTitle() { return name; }
    public String getContent() { return instructions; }
    
    public String getName() { return name; }
    public String getType() { return type; }
    public String getMuscle() { return muscle; }
    public String getDifficulty() { return difficulty; }
    public String getInstructions() { return instructions; }
    public String getEquipment() { return equipment; }
    public String getSafetyInfo() { return safety_info; }
}
