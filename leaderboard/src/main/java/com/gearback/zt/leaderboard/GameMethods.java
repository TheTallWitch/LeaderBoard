package com.gearback.zt.leaderboard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.gearback.methods.HttpPostRequest;
import com.gearback.methods.Methods;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameMethods {
    String domain = "https://www.zodtond.com/";
    Classes classes = new Classes();
    Methods methods = new Methods();

    public void GetUserScore(String appToken, String userToken, String userName, int userImage, final ScoreListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (!result.equals("")) {
                    try {
                        JSONObject mainObject = new JSONObject(result);
                        int high = 0;
                        if (mainObject.has("high")) {
                            high = mainObject.getInt("high");
                        }

                        Classes.Score userScore = classes.new Score(mainObject.getString("user"), mainObject.getInt("level"), mainObject.getInt("score"), mainObject.getInt("goal"), high);
                        listener.onResult(userScore);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        postRequest.execute(domain + "app_upload/applications/userexperience/api/v2/", "ud=" + userToken + "&token=" + appToken + "&name=" + userImage + "|" + userName);
    }
    public void ParseScoreResult(String result, ScoreListener listener) {
        try {
            JSONObject mainObject = new JSONObject(result);
            int high = 0;
            if (mainObject.has("high")) {
                high = mainObject.getInt("high");
            }

            Classes.Score userScore = classes.new Score(mainObject.getString("user"), mainObject.getInt("level"), mainObject.getInt("score"), mainObject.getInt("goal"), high);
            listener.onResult(userScore);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void GetLeaderBoard(final Activity activity, String appToken, String userToken, final LeaderBoardListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (!result.equals("")) {
                    methods.writeToFile("leaderboard.ini", result, activity);
                    try {
                        JSONObject mainObject = new JSONObject(result);
                        JSONArray table = mainObject.getJSONArray("highscore");
                        List<Classes.LeaderBoard> leaderBoards = new ArrayList<>();
                        for (int i = 0; i < table.length(); i++) {
                            JSONObject item = table.getJSONObject(i);
                            int high = 0;
                            if (item.has("high")) {
                                high = item.getInt("high");
                            }
                            leaderBoards.add(classes.new LeaderBoard(item.getString("token"), item.getString("name"), item.getInt("level"), item.getInt("score"), high, false));
                        }
                        listener.onResult(leaderBoards, mainObject.getInt("rank"));
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        postRequest.execute(domain + "app_upload/applications/userexperience/api/v2/", "limit=25&call=highscore&ud=" + userToken + "&token=" + appToken);
    }
    public void ParseLeaderBoardResult(String result, LeaderBoardListener listener) {
        try {
            JSONObject mainObject = new JSONObject(result);
            JSONArray table = mainObject.getJSONArray("highscore");
            List<Classes.LeaderBoard> leaderBoards = new ArrayList<>();
            for (int i = 0; i < table.length(); i++) {
                JSONObject item = table.getJSONObject(i);
                int high = 0;
                if (item.has("high")) {
                    high = item.getInt("high");
                }
                leaderBoards.add(classes.new LeaderBoard(item.getString("token"), item.getString("name"), item.getInt("level"), item.getInt("score"), high, false));
            }
            listener.onResult(leaderBoards, mainObject.getInt("rank"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void GetAvatarCount(String ud, final AvatarListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    if (!result.equals("")) {
                        JSONObject mainObject = new JSONObject(result);
                        String publicImages = mainObject.getString("public");
                        String myImages = mainObject.getString("my");
                        String[] list = publicImages.split(",");
                        String[] myList = myImages.split(",");
                        listener.onResult(list, myList, publicImages, myImages);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        postRequest.execute(domain + "app_upload/applications/games/avatars/api/", "ud=" + ud);
    }

    public void GetExperience(String appToken, String appact, String userToken, int score, final ResultListener listener) {
        String scoreString = String.valueOf(score);
        if (score == -1) {
            scoreString = "";
        }
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                listener.onResult(result);
            }
        });
        postRequest.execute(domain + "app_upload/applications/userexperience/api/v2/", "ud=" + userToken + "&token=" + appToken + "&score=" + scoreString + "&act=" + appact);
    }

    public void GetFriends(final String appToken, final String userToken, final int act, final FriendListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (!result.equals("")) {
                    ParseFriendsResult(result, act, userToken, listener);
                }
            }
        });
        postRequest.execute(domain + "app_upload/applications/games/API/followers/", "ud=" + userToken);
    }
    public void ParseFriendsResult(String result, int act, String userToken, FriendListener listener) {
        if (!result.equals("")) {
            try {
                JSONObject mainObject = new JSONObject(result);
                if (act == 1) {
                    if (mainObject.getString("status").equals("added")) {
                        List<Classes.Friend> friendList = new ArrayList<>();
                        List<Classes.Friend> followerList = new ArrayList<>();
                        String users = userToken + ",", followerString = "";
                        JSONArray friends = mainObject.getJSONArray("friends");
                        JSONArray followers = mainObject.getJSONArray("followers");
                        for (int i = 0; i < friends.length(); i++) {
                            JSONObject item = friends.getJSONObject(i);
                            friendList.add(classes.new Friend(item.getString("token"), item.getString("user")));
                            users += item.getString("token") + ",";
                        }
                        for (int i = 0; i < followers.length(); i++) {
                            JSONObject item = followers.getJSONObject(i);
                            followerList.add(classes.new Friend(item.getString("token"), item.getString("user")));
                            followerString += item.getString("token") + ",";
                        }
                        if (!users.equals("")) {
                            users = users.substring(0, users.length() - 1);
                        }
                        if (!followerString.equals("")) {
                            followerString = followerString.substring(0, followerString.length() - 1);
                        }

                        listener.onResult(friendList, followerList, users, followerString);
                    }
                    else {
                        listener.onFail();
                    }
                }
                else if (act == 2) {
                    if (mainObject.getString("status").equals("removed")) {
                        String users = userToken + ",", followerString = "";
                        List<Classes.Friend> friendList = new ArrayList<>();
                        List<Classes.Friend> followerList = new ArrayList<>();
                        JSONArray friends = mainObject.getJSONArray("friends");
                        JSONArray followers = mainObject.getJSONArray("followers");
                        for (int i = 0; i < friends.length(); i++) {
                            JSONObject item = friends.getJSONObject(i);
                            friendList.add(classes.new Friend(item.getString("token"), item.getString("user")));
                            users += item.getString("token") + ",";
                        }
                        for (int i = 0; i < followers.length(); i++) {
                            JSONObject item = followers.getJSONObject(i);
                            followerList.add(classes.new Friend(item.getString("token"), item.getString("user")));
                            followerString += item.getString("token") + ",";
                        }
                        if (!users.equals("")) {
                            users = users.substring(0, users.length() - 1);
                        }
                        if (!followerString.equals("")) {
                            followerString = followerString.substring(0, followerString.length() - 1);
                        }

                        listener.onResult(friendList, followerList, users, followerString);
                    }
                    else {
                        listener.onFail();
                    }
                }
                else if (act == 0) {
                    if (mainObject.getString("status").equals("ok")) {
                        String users = userToken + ",", followerString = "";
                        List<Classes.Friend> friendList = new ArrayList<>();
                        List<Classes.Friend> followerList = new ArrayList<>();
                        JSONArray friends = mainObject.getJSONArray("friends");
                        JSONArray followers = mainObject.getJSONArray("followers");
                        for (int i = 0; i < friends.length(); i++) {
                            JSONObject item = friends.getJSONObject(i);
                            friendList.add(classes.new Friend(item.getString("token"), item.getString("user")));
                            users += item.getString("token") + ",";
                        }
                        for (int i = 0; i < followers.length(); i++) {
                            JSONObject item = followers.getJSONObject(i);
                            followerList.add(classes.new Friend(item.getString("token"), item.getString("user")));
                            followerString += item.getString("token") + ",";
                        }
                        if (!users.equals("")) {
                            users = users.substring(0, users.length() - 1);
                        }
                        if (!followerString.equals("")) {
                            followerString = followerString.substring(0, followerString.length() - 1);
                        }

                        listener.onResult(friendList, followerList, users, followerString);
                    }
                    else {
                        listener.onFail();
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void GetFriendRanks(String appToken, String userPublicToken, final String users, final LeaderBoardListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (!result.equals("")) {
                    try {
                        JSONObject mainObject = new JSONObject(result);
                        JSONArray table = mainObject.getJSONArray("highscore");
                        List<Classes.LeaderBoard> friendLeaderBoard = new ArrayList<>();
                        friendLeaderBoard.clear();
                        for (int i = 0; i < table.length(); i++) {
                            JSONObject item = table.getJSONObject(i);
                            int high = 0;
                            if (item.has("high")) {
                                high = item.getInt("high");
                            }
                            friendLeaderBoard.add(classes.new LeaderBoard(item.getString("token"), item.getString("name"), item.getInt("level"), item.getInt("score"), high, true));
                        }

                        Collections.sort(friendLeaderBoard);
                        listener.onResult(friendLeaderBoard, 0);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        postRequest.execute(domain + "app_upload/applications/userexperience/api/v2/", "call=highscore&ud=" + userPublicToken + "&token=" + appToken + "&users=" + users);
    }
    public void AddFriend(String userToken, String identifier, final ResultListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                listener.onResult(result);
            }
        });
        postRequest.execute(domain + "app_upload/applications/games/API/followers/", "act=submit&ud=" + userToken + "&value=" + identifier);
    }
    public void RemoveFriend(String userToken, String identifier, final ResultListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                listener.onResult(result);
            }
        });
        postRequest.execute(domain + "app_upload/applications/games/API/followers/", "act=remove&ud=" + userToken + "&token=" + identifier);
    }

    public void GetFollowerRanks(String appToken, String userPublicToken, final String users, final LeaderBoardListener listener) {
        HttpPostRequest postRequest = new HttpPostRequest(null, new HttpPostRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (!result.equals("")) {
                    try {
                        JSONObject mainObject = new JSONObject(result);
                        JSONArray table = mainObject.getJSONArray("highscore");
                        List<Classes.LeaderBoard> followerLeaderBoard = new ArrayList<>();
                        for (int i = 0; i < table.length(); i++) {
                            JSONObject item = table.getJSONObject(i);
                            int high = 0;
                            if (item.has("high")) {
                                high = item.getInt("high");
                            }
                            followerLeaderBoard.add(classes.new LeaderBoard(item.getString("token"), item.getString("name"), item.getInt("level"), item.getInt("score"), high, false));
                        }

                        Collections.sort(followerLeaderBoard);
                        listener.onResult(followerLeaderBoard, 0);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        postRequest.execute(domain + "app_upload/applications/userexperience/api/v2/", "call=highscore&ud=" + userPublicToken + "&token=" + appToken + "&users=" + users);
    }

    public void PopulateImage(final Activity activity, final int imageId, final ImageView imageView) {
        Picasso.with(activity)
                .load(Uri.parse(domain + activity.getString(R.string.avatar_url) + imageId))
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(activity)
                                .load(Uri.parse(domain + activity.getString(R.string.avatar_url) + imageId))
                                .placeholder(R.drawable.image_0)
                                .error(R.drawable.image_0)
                                .into(imageView);
                    }
                });
    }

    public interface ResultListener {
        public void onResult(String result);
    }
    public interface ScoreListener {
        public void onResult(Classes.Score score);
    }
    public interface LeaderBoardListener {
        public void onResult(List<Classes.LeaderBoard> leaderBoards, int userRank);
    }
    public interface AvatarListener {
        public void onResult(String[] publicImages, String[] privateImages, String images, String myImages);
    }
    public interface FriendListener {
        public void onResult(List<Classes.Friend> friends, List<Classes.Friend> followers, String friendsString, String followersString);
        public void onFail();
    }

    public interface BackListener {
        public void onBack();
    }

    public interface LoginListener {
        public void onClick();
    }
}
