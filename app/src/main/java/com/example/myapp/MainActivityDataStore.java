package com.example.myapp;

import java.util.HashMap;
import java.util.Set;

/**
 * assuming group name unique, map group name to group datastore
 */
public class MainActivityDataStore {
  private final HashMap<String, GroupDataStore> groupDataMap;   // groupName -> groupDataStore
  private final HashMap<String, String> codeNameMap;            // joinCode -> groupName

  /**
   * init new group activity if not exist
   * @param groupName groupName as string
   */
  public void createNewGroupActivity(String groupName) {
    if (!groupDataMap.containsKey(groupName)) {
      GroupDataStore store = new GroupDataStore(groupName);
      groupDataMap.put(groupName, store);
      codeNameMap.put(store.getJoinCode(), groupName);
    }
  }

  public void joinNewGroupActivity(String groupName, String joinCode) {
    if (!groupDataMap.containsKey(groupName)) {
      groupDataMap.put(groupName, new GroupDataStore(groupName, joinCode));
    }
  }

  public Set<String> getAllGroupNames() {
    return groupDataMap.keySet();
  }

  /**
   * find group name using join code
   * @param joinCode join code
   * @return group name if join code valid, null otherwise
   */
  public String findGroupByCode(String joinCode) {
    return codeNameMap.get(joinCode);
  }

  /**
   * @param groupName name of the group to get
   * @return corresponding group data store
   */
  public GroupDataStore getGroupDataStore(String groupName) { return groupDataMap.get(groupName); }

  private MainActivityDataStore() {
    groupDataMap = new HashMap<>();
    codeNameMap = new HashMap<>();
    // initialize with some hardcoded data
    String name1 = "Sid's Group";
    String code1 = "1Q2W3E";
    codeNameMap.put(code1, name1);
  }

  public static MainActivityDataStore getInstance() { return holder; }
  private static final MainActivityDataStore holder = new MainActivityDataStore();
}
