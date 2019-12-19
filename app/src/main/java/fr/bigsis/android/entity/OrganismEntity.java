package fr.bigsis.android.entity;

import java.util.HashMap;
import java.util.Map;

public class OrganismEntity {

    String groupName;
    String idGroup;

    public OrganismEntity() {
    }

    public OrganismEntity(String groupName) {
        this.groupName = groupName;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }
}
