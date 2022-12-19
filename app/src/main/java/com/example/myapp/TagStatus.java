package com.example.myapp;

enum TagStatus {
  NONE("#FFF6F6F6"),
  IN_PROGRESS("#FF3700B3"),
  URGENT("#FFFFCA18"),
  DONE("#FF00FF00"),
  ARCHIVED("#FF585858");

  private final String color;

  TagStatus(String action) {
    this.color = action;
  }

  public String getColor() {
    return this.color;
  }
}
