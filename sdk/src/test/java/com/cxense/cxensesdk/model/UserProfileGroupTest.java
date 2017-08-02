package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
public class UserProfileGroupTest {
    private UserProfileGroup profileGroup;

    @Before
    public void setUp() throws Exception {
        profileGroup = new UserProfileGroup();
    }

    @Test
    public void getGroup() throws Exception {
        String group = "group";
        profileGroup.group = group;
        assertEquals(group, profileGroup.getGroup());
    }

    @Test
    public void getCount() throws Exception {
        int count = 1;
        profileGroup.count = count;
        assertEquals(count, profileGroup.getCount());
    }

    @Test
    public void getWeight() throws Exception {
        double weight = 1;
        profileGroup.weight = weight;
        assertThat(weight, equalTo(profileGroup.getWeight()));
    }
}