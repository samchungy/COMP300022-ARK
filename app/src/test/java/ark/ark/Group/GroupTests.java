package ark.ark.Group;

import android.location.Location;

import org.junit.Test;

import ark.ark.Groups.Friend;
import ark.ark.Groups.Group;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GroupTests {
    @Test
    public void GroupObjectOwnerCreatedCorrectly() throws Exception {
        String owner = "test@test.com";
        String id = "1";
        Group testGroup = new Group(id, owner);
        assertEquals(testGroup.getOwner(), owner);
    }

    @Test
    public void GroupObjectIdCreatedCorrectly() throws Exception {
        String owner = "test@test.com";
        String id = "1";
        Group testGroup = new Group(id, owner);
        assertEquals(testGroup.getId(), id);
    }

    @Test
    public void GroupAddingOwnerToFriendsNotAllowed() throws Exception {
        // If the owner of the group themselves is attempted to be added to a group,
        // it wouldn' be allowed and the friendlist would remain empty.

        String ownerEmail = "test@test.com";
        String id = "1";
        Group testGroup = new Group(id, ownerEmail);
        Friend owner = new Friend(ownerEmail);
        testGroup.updateFriend(owner);
        assertEquals(testGroup.getFriends().size(), 0);
    }

    @Test
    public void GroupAddsFriend() throws Exception {
        // Adding a friend to the group puts it in the friendlist

        String ownerEmail = "test@test.com";
        String friendEmail = "friend@test.com";
        String id = "1";
        Group testGroup = new Group(id, ownerEmail);
        Friend friend = new Friend(friendEmail);
        testGroup.updateFriend(friend);
        assertEquals(testGroup.getFriends().get(friendEmail), friend);
    }

    @Test
    public void GroupAddsFriendDouble() throws Exception {
        // Adding a friend twice to the group puts it in the friendlist once

        String ownerEmail = "test@test.com";
        String friendEmail = "friend@test.com";
        String id = "1";
        Group testGroup = new Group(id, ownerEmail);
        Friend friend = new Friend(friendEmail);
        testGroup.updateFriend(friend);
        testGroup.updateFriend(friend);
        assertEquals(testGroup.getFriends().size(), 1);
    }


    @Test
    public void FriendEmailCorrect() throws Exception {
        // Adding a friend twice to the group puts it in the friendlist once
        String friendEmail = "friend@test.com";

        Friend friend = new Friend(friendEmail);

        assertEquals(friend.getEmail(), friendEmail);
    }


}