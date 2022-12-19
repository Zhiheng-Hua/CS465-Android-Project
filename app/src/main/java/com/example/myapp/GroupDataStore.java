package com.example.myapp;

import android.util.Pair;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * store data of ONE group by storing map of question -> proposal list
 * question is identify by the view id of the question (generated using ViewCompat.generateViewId())
 */
public class GroupDataStore {
  // map questionId -> proposalsList
  private final HashMap<Integer, ArrayList<ProposalTagInfo>> questionProposalMap;
  // map questionId -> default emoji set of the question
  private final HashMap<Integer, HashMap<String, Pair<Integer, Boolean>>> questionEmojiSetMap;
  // map questionId -> questionStatus
  private final HashMap<Integer, TagStatus> questionStatusMap;
  // map questionId -> question Title
  private final HashMap<Integer, String> questionTitleMap;
  private final String joinCode;
  private final String groupName;

  /**
   * init data store of a new question if not yet exist
   * @param questionId questionId of the question
   * @param emojiSet default emoji set of the question
   */
  public void initNewQuestionActivity(Integer questionId, HashMap<String, Pair<Integer, Boolean>> emojiSet) {
    if (!questionProposalMap.containsKey(questionId)) {
      questionProposalMap.put(questionId, new ArrayList<>());
    }
    if (!questionEmojiSetMap.containsKey(questionId)) {
      questionEmojiSetMap.put(questionId, new HashMap<>(emojiSet));
    }
    if (!questionStatusMap.containsKey(questionId)) {
      questionStatusMap.put(questionId, TagStatus.NONE);
    }
  }

  public void setQuestionStatus(Integer questionId, TagStatus status) {
    questionStatusMap.put(questionId, status);
  }

  public TagStatus getQuestionStatus(Integer questionId) {
    return questionStatusMap.get(questionId);
  }

  public void setQuestionTitle(Integer questionId, String title) {
    questionTitleMap.put(questionId, title);
  }

  public String getQuestionTitle(Integer questionId) {
    return questionTitleMap.get(questionId);
  }

  // should use return value as read only object
  public HashMap<Integer, HashMap<String, Pair<Integer, Boolean>>> getQuestionEmojiSetMap() {
    return questionEmojiSetMap;
  }

  public HashMap<String, Pair<Integer, Boolean>> getDefaultEmojiSet(Integer questionId) {
    HashMap<String, Pair<Integer, Boolean>> res = questionEmojiSetMap.get(questionId);
    if (res == null) {
      throw new InvalidParameterException("question does not exist");
    }
    return res;
  }

  /**
   * delete question from data store
   * @param questionId id of the question to delete
   */
  public void deleteQuestionActivity(Integer questionId) {
    questionProposalMap.remove(questionId);
    questionEmojiSetMap.remove(questionId);
    questionStatusMap.remove(questionId);
    questionTitleMap.remove(questionId);
  }

  /**
   * put a new proposal to existing question, update existing one if questionId already exist
   * @param questionId id of the question to add to
   * @param proposalInfo proposal info
   */
  public void putProposal(Integer questionId, ProposalTagInfo proposalInfo) {
    if (questionProposalMap.containsKey(questionId)) {
      ArrayList<ProposalTagInfo> list = questionProposalMap.get(questionId);
      assert list != null;
      for (ProposalTagInfo info : list) {
        if (info.getId() == proposalInfo.getId()) {
          info.updateProposal(proposalInfo);
          return;
        }
      }
      list.add(proposalInfo);
    } else {
      throw new InvalidParameterException("question does not exist");
    }
  }

  /**
   * remove proposal from existing question
   * @param questionId id of the question to remove from
   * @param proposalId id of the proposal to remove
   */
  public void removeProposal(Integer questionId, int proposalId) {
    if (questionProposalMap.containsKey(questionId)) {
      ArrayList<ProposalTagInfo> list = questionProposalMap.get(questionId);
      assert list != null;
      for (ProposalTagInfo info : list) {
        if (info.getId() == proposalId) {
          list.remove(info);
          return;
        }
      }
      throw new InvalidParameterException("proposal does not exist");
    } else {
      throw new InvalidParameterException("question does not exist");
    }
  }

  /**
   * get proposalsList of an existing question
   * @param questionId id of the question to get
   * @return proposalsList of the question, or null if question not exist
   */
  public ArrayList<ProposalTagInfo> getProposalsList(Integer questionId) {
    ArrayList<ProposalTagInfo> res = questionProposalMap.get(questionId);
    if (res == null) {
      throw new InvalidParameterException("question does not exist");
    }
    return res;
  }

  public GroupDataStore(String name) {
    questionProposalMap = new HashMap<>();
    questionEmojiSetMap = new HashMap<>();
    questionStatusMap = new HashMap<>();
    questionTitleMap = new HashMap<>();
    joinCode = generateJoinCode();
    groupName = name;
  }

  // this constructor allow us to control join code for demo
  public GroupDataStore(String name, String code) {
    questionProposalMap = new HashMap<>();
    questionEmojiSetMap = new HashMap<>();
    questionStatusMap = new HashMap<>();
    questionTitleMap = new HashMap<>();
    joinCode = code;
    groupName = name;
  }

  private String generateJoinCode() {
    Random rng = new Random();
    char[] base = Integer.toString(rng.nextInt(900000) + 100000).toCharArray();
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    int lc = rng.nextInt(7);    // random number of letter
    for (int i = 0; i < lc; i++) {
      int idx = rng.nextInt(6);   // position to change to letter
      base[idx] = alphabet.charAt(rng.nextInt(52));
    }
    return String.valueOf(base);
  }

  public String getJoinCode() { return joinCode; }
  public String getGroupName() { return groupName; }
}
