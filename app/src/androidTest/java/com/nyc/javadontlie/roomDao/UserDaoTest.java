package com.nyc.javadontlie.roomDao;

import android.arch.persistence.room.Room;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.nyc.javadontlie.MoneyActivity;
import com.nyc.javadontlie.RegisterActivity;
import com.nyc.javadontlie.database.AppDatabase;
import com.nyc.javadontlie.moneyModel.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Wayne Kellman on 2/24/18.
 */
@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

    AppDatabase appDatabase;
    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(
            RegisterActivity.class);
    @Before
    public void init(){
        /**
         * Room.databaseBuilder(getApplicationContext(),
         AppDatabase.class, "Users").build();
         */
        appDatabase = Room.inMemoryDatabaseBuilder(mActivityRule.getActivity(),AppDatabase.class).build();
    }

    @After
    public void close(){
        appDatabase.close();
    }

    @Test
    public void ReadandWriteTest() {
        User user = new User();
        user.setUserName("TestUser");
        appDatabase.userDao().insertAll(user);
        List<User> userList = appDatabase.userDao().getAll();
        assertEquals(userList.get(0).getUserName(),"TestUser");
    }

    @Test
    public void appToDatabase() {
        List<User> userListBase = appDatabase.userDao().getAll();
        appDatabase.userDao().insertAll(new User());
        List<User> userListCheck = new ArrayList<>();
        userListCheck.add(new User());
        assertEquals(userListBase.size() + 1,userListCheck.size());

    }
}