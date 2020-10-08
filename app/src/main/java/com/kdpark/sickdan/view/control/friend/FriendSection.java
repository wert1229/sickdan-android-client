package com.kdpark.sickdan.view.control.friend;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class FriendSection extends ExpandableGroup<FriendItem> {
    public FriendSection(String title, List<FriendItem> items) {
        super(title, items);
    }
}
