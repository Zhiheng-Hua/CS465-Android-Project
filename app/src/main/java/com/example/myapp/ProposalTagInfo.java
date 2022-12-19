package com.example.myapp;

import android.util.Pair;

import java.util.HashMap;
import java.util.Objects;

public class ProposalTagInfo {
  private String title;
  private HashMap<String, Pair<Integer, Boolean>> emojiEntries; // emoji code -> {count, isClicked}
  private int id;

  public ProposalTagInfo(int id, String title, HashMap<String, Pair<Integer, Boolean>> emojiEntries) {
    this.id = id;
    this.title = title;
    this.emojiEntries =  new HashMap<>(emojiEntries);
  }

  /**
   * shallow copy input info
   * @param info ProposalTagInfo to copy
   */
  public void updateProposal(ProposalTagInfo info) {
    this.id = info.getId();
    this.title = info.getTitle();
    this.emojiEntries = info.getEmojiEntries();
  }

  /**
   * fine emoji and increment emoji count, set isClicked to true
   * @param emojiCode emojiCode string
   * @return new emoji count, null if emoji doesn't exist
   */
  public Integer upVoteEmoji(String emojiCode) {
    if (emojiEntries.containsKey(emojiCode)) {
      emojiEntries.put(emojiCode, new Pair<>(Objects.requireNonNull(emojiEntries.get(emojiCode)).first + 1, true));
      return Objects.requireNonNull(emojiEntries.get(emojiCode)).first;
    }
    return null;
  }

  /**
   * fine emoji and decrement emoji count, set isClicked to false
   * @param emojiCode emojiCode string
   * @return new emoji count, null if emoji doesn't exist
   */
  public Integer downVoteEmoji(String emojiCode) {
    if (emojiEntries.containsKey(emojiCode)) {
      emojiEntries.put(emojiCode, new Pair<>(Objects.requireNonNull(emojiEntries.get(emojiCode)).first - 1, false));
      return Objects.requireNonNull(emojiEntries.get(emojiCode)).first;
    }
    return null;
  }

  public String getTitle() { return title; }
  public HashMap<String, Pair<Integer, Boolean>> getEmojiEntries() { return emojiEntries; }
  public int getId() { return id; }
  public void setTitle(String title) { this.title = title; }
  public void setEmojiEntries(HashMap<String, Pair<Integer, Boolean>> emojiEntries) { this.emojiEntries = emojiEntries; }
  public void setId(int id) { this.id = id; }
}
