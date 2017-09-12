package ark.ark;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import ark.ark.Authentication.ARK_auth;
import ark.ark.Profile.ProfileCreationActivity;
import butterknife.ButterKnife;
import ark.ark.Chat.ChatFragment;
import butterknife.OnClick;
import layout.HomeFragment;
import layout.MapFragment;


// the main activity
public class HomeActivity extends AppCompatActivity {


    private Fragment frag_home = new HomeFragment();
    private Fragment frag_chat = new ChatFragment();
    private Fragment frag_map = new MapFragment();
    private Fragment frag_profile = new ProfileFragment();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchTo("Home");
                    return true;
                case R.id.navigation_chat:
                    switchTo("Chat");
                    return true;
                case R.id.navigation_map:
                    switchTo("Map");
                    Intent myIntent = new Intent(HomeActivity.this, MapNavDrawer.class);
                    startActivity(myIntent);
                    return true;
                case R.id.navigation_profile:
                    switchTo("Profile");
                    //Intent myIntent2 = new Intent(HomeActivity.this, ArActivity.class);
                    //startActivity(myIntent2);
                    Intent myIntent2 = new Intent(HomeActivity.this, ProfileCreationActivity.class);
                    startActivity(myIntent2);
                    return true;
            }
            return false;
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ButterKnife.bind(this);
        //TODO

        // switch to home page after entering the app
        switchTo("Home");
    }





    // switching to different pages
    private void switchTo(String page) {
        Fragment targetFrag;

        switch (page) {
            case "Home":
                targetFrag = frag_home;
                break;
            case "Chat":
                targetFrag = frag_chat;
                break;
            case "Map":
                targetFrag = frag_map;
                break;
            case "Profile":
                targetFrag = frag_profile;
                break;
            default:
                targetFrag = frag_home;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, targetFrag).commit();
    }



    public void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }




    // test switching users
    public void austinTest_switchToUser1(View v) {
        ARK_auth.storeUserEmail("user1@user1.com", this);
        showToast("congrats folk! You switched to user1!");
        showToast(ARK_auth.fetchUserEmail(this));
    }

    public void austinTest_switchToUser2(View v) {
        ARK_auth.storeUserEmail("user2@user2.com", this);
        showToast("ughhhh! You switched to user2!");
        showToast(ARK_auth.fetchUserEmail(this));
    }
}
