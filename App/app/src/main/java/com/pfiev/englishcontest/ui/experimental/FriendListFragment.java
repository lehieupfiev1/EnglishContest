package com.pfiev.englishcontest.ui.experimental;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.pfiev.englishcontest.R;
import com.pfiev.englishcontest.adapter.BlockListAdapter;
import com.pfiev.englishcontest.adapter.FriendListAdapter;
import com.pfiev.englishcontest.adapter.UsersListAdapter;
import com.pfiev.englishcontest.adapter.WaitingListAdapter;
import com.pfiev.englishcontest.databinding.FragmentExperimentalFriendListBinding;
import com.pfiev.englishcontest.firestore.UsersCollection;
import com.pfiev.englishcontest.model.BlockItem;
import com.pfiev.englishcontest.model.FriendItem;
import com.pfiev.englishcontest.model.UserItem;
import com.pfiev.englishcontest.model.WaitingItem;
import com.pfiev.englishcontest.realtimedb.BlockList;
import com.pfiev.englishcontest.realtimedb.FriendList;
import com.pfiev.englishcontest.realtimedb.WaitingList;
import com.pfiev.englishcontest.utils.SharePreferenceUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment {

    private FragmentExperimentalFriendListBinding binding;
    LottieAnimationView loadingAnim;
    HashMap<Integer, String> typeSelect = new HashMap<Integer, String>();
    // hash to compare to check if allow to display result after get from server
    private String inDisplayHash;
    // is loading more process
    private boolean isLoadingMore;
    // is search with name in text box
    private boolean isSearchWithName;

    // display type eg FRIENDS, WAITING, ...
    private String displayType;
    // keywords to search
    private String keywords;
    // block list adapter
    private BlockListAdapter blockListAdapter;
    // waiting list adapter
    private WaitingListAdapter waitingListAdapter;
    // all users list adapter
    private UsersListAdapter usersListAdapter;


    public FriendListFragment() {
        typeSelect.put(1, "FRIENDS");
        typeSelect.put(2, "WAITING");
        typeSelect.put(3, "BLOCK");
        typeSelect.put(4, "SEARCH");
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExperimentalFriendListBinding.inflate(
                inflater, container, false);

        // Declare loading animation and hide it
        loadingAnim = binding.friendsListLoading;
        loadingAnim.setRepeatCount(LottieDrawable.INFINITE);

        // Binding to button
        this.bindingBackButton();
        this.bindTypeSelect();
        this.initScrollListener();
        this.bindSearchButton();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Check type friend when initialize
        binding.friendsListTypeSelect.check(
                binding.friendsListTypeSelect.getChildAt(0).getId());
    }

    /**
     * Binding back button click event, back to main fragment
     */
    private void bindingBackButton() {
        // Back to main when touch return button
        binding.experimentalFriendsBackBtn.setOnClickListener(view -> getParentFragmentManager().beginTransaction()
                .replace(R.id.experimental_fullscreen_content, MainFragment.class, null)
                .commitNow());
    }

    /**
     * Set action when choose type in radio group
     */
    private void bindTypeSelect() {
        binding.friendsListTypeSelect.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        View radioButton = radioGroup.findViewById(i);
                        int idx = radioGroup.indexOfChild(radioButton) + 1;
                        changeChoiceDecor(idx);
                        displayType = typeSelect.get(idx);
                        showLoadingAnim();
                        isSearchWithName = false;
                        loadDataMore(displayType, true);
                    }
                });

    }

    /**
     * Show loading animation
     */
    private void showLoadingAnim() {
        isLoadingMore = true;
        loadingAnim.setVisibility(View.VISIBLE);
        loadingAnim.playAnimation();
    }

    /**
     * Hide loading animation
     */
    private void hideLoadingAnim() {
        loadingAnim.setVisibility(View.INVISIBLE);
        loadingAnim.pauseAnimation();
        isLoadingMore = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detach listener
        FriendList friendList = FriendList.getInstance();
        friendList.detachFriendStatusListener();
        Log.d("Tag", "FriendListFragment.onDestroyView() has been called.");
        binding.friendsListTypeSelect.removeAllViews();
        binding.friendsListTypeSelect.removeAllViewsInLayout();
        binding.friendsListTypeSelect.clearDisappearingChildren();
    }

    /**
     * Change Radio Button when selected once
     *
     * @param selectedIndex
     */
    private void changeChoiceDecor(int selectedIndex) {
        int choices = binding.friendsListTypeSelect.getChildCount();
        RadioButton radioButton;
        // Set background for every
        for (int i = 0; i < choices; i++) {
            int backgroundIndex = R.drawable.radio_btn_friends_list_mid;
            if (i == 0) {
                backgroundIndex = R.drawable.radio_btn_friends_list_first;
            } else if (i == choices - 1) {
                backgroundIndex = R.drawable.radio_btn_friends_list_last;
            }
            radioButton = (RadioButton) binding.friendsListTypeSelect.getChildAt(i);
            radioButton.setBackgroundResource(backgroundIndex);
        }
        // Set background for selected type
        int backgroundSelectedIndex = R.drawable.radio_btn_friends_list_mid_active;
        if (selectedIndex == 1) {
            backgroundSelectedIndex = R.drawable.radio_btn_friends_list_first_active;
        } else if (selectedIndex == choices) {
            backgroundSelectedIndex = R.drawable.radio_btn_friends_list_last_active;
        }
        radioButton = (RadioButton) binding.friendsListTypeSelect.getChildAt(selectedIndex - 1);
        radioButton.setBackgroundResource(backgroundSelectedIndex);

    }

    /**
     * Generate and return new display hash
     *
     * @return
     */
    private String getDisplayHash() {
        byte[] array = new byte[8]; // length is bounded by 7
        new Random().nextBytes(array);
        this.inDisplayHash = new String(array, Charset.forName("UTF-8"));
        return inDisplayHash;
    }

    /**
     * Check if allow to display
     *
     * @param displayHash
     * @return
     */
    private boolean allowToDisplay(String displayHash) {
        return displayHash.equals(this.inDisplayHash);
    }

    private void bindSearchButton() {
        binding.friendsListSearchBoxBtn.setOnClickListener(view -> {
            String keyword = binding.friendsListSearchBoxEdt.getText().toString();
            if (keyword.length() < 4) {
                isSearchWithName = false;
                if (keyword.isEmpty()) {
                    loadDataMore(displayType, true);
                } else
                    Toast.makeText(getContext(),
                            getString(R.string.friend_list_not_enough_character)
                            , Toast.LENGTH_LONG).show();
            } else {
                isSearchWithName = true;
                keywords = keyword;
                loadDataMore(displayType, true);
            }
        });
    }

    /**
     * Scroll view listener to load more data when scroll to bottom
     */
    private void initScrollListener() {
        binding.friendsListRecycleLv
                .setLayoutManager(new LinearLayoutManager(getContext()));
        binding.friendsListRecycleLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoadingMore) {
                    if (linearLayoutManager != null
                            &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                    binding.friendsListRecycleLv.getAdapter().getItemCount() - 5) {
                        //bottom of list!
                        isLoadingMore = true;
                        loadDataMore(displayType, false);
                    }
                }
            }
        });
    }

    /**
     * Load more data when
     *
     * @param displayType
     * @param reCreate
     */
    private void loadDataMore(String displayType, boolean reCreate) {
        switch (displayType) {
            case "FRIENDS":
                if (reCreate) loadFriendList();
                break;
            case "WAITING":
                loadWaitingList(reCreate);
                break;
            case "BLOCK":
                loadBlockList(reCreate);
                break;
            case "SEARCH":
                loadAllUsers(reCreate);
                break;
        }
    }

    /**
     * Load Block list
     *
     * @param reCreate
     */
    public void loadBlockList(boolean reCreate) {
        if (blockListAdapter == null || reCreate) {
            blockListAdapter = new BlockListAdapter(getContext());
        }
        BlockList blockList = BlockList.getInstance();
        String ownerUid = SharePreferenceUtils.getUserData(getActivity()).getUserId();
        blockList.setUid(ownerUid);
        String displayHash = getDisplayHash();
        int limit = 10;
        BlockList.BlockListProcess dataProcess = new BlockList.BlockListProcess() {
            @Override
            public void process(List<BlockItem> blockItemList) {
                if (allowToDisplay(displayHash)) {
                    blockListAdapter.addData(blockItemList);
                    if (blockItemList.isEmpty()) {
                        if (reCreate && isSearchWithName) {
                            showToastNotFound();
                        }
                    } else {
                        if (isSearchWithName) {
                            blockListAdapter.setStartAtName(
                                    blockItemList.get(blockItemList.size() - 1).getName());
                        } else {
                            blockListAdapter.setStartAtTime(
                                    blockItemList.get(blockItemList.size() - 1).getTimestamp());
                            if (blockListAdapter.getStartAtTime() == 0l
                                    && blockListAdapter.getItemCount() > 0 ) return;
                        }
                    }
                    hideLoadingAnim();
                    if (reCreate) {
                        binding.friendsListRecycleLv.setAdapter(blockListAdapter);
                    }
                }

            }
        };
        if (!isSearchWithName) {
            blockList.getListOrderByTime(blockListAdapter.getStartAtTime(), limit, dataProcess);
        } else {
            if (reCreate) blockListAdapter.setStartAtName(keywords);
            blockList.getListSearchByName(keywords,
                    blockListAdapter.getStartAtName(), limit, dataProcess);
        }
    }

    /**
     * Load friend list
     */
    public void loadFriendList() {
        FriendListAdapter adapter = new FriendListAdapter(getContext());
        adapter.setRecyclerView(binding.friendsListRecycleLv);
        FriendList friendList = FriendList.getInstance();
        friendList.listenFriendStatus(adapter);
        String displayHash = getDisplayHash();
        FriendList.FriendListProcess listProcess = new FriendList.FriendListProcess() {
            @Override
            public void process(List<FriendItem> friendItemList) {
                if (allowToDisplay(displayHash)) {
                    adapter.setData(friendItemList);
                    binding.friendsListRecycleLv.setAdapter(adapter);
                    hideLoadingAnim();
                }
            }
        };
        if (isSearchWithName) {
            friendList.getListFriendsWithName(keywords, listProcess);
        } else {
            friendList.getListFriends(listProcess);
        }

    }

    /**
     * Load Waiting list
     *
     * @param reCreate
     */
    public void loadWaitingList(boolean reCreate) {
        if (waitingListAdapter == null || reCreate) {
            waitingListAdapter = new WaitingListAdapter(getContext());
        }

        WaitingList waitingList = WaitingList.getInstance();
        String ownerUid = SharePreferenceUtils.getUserData(getActivity()).getUserId();
        waitingList.setUid(ownerUid);
        String displayHash = getDisplayHash();
        int limit = 10;
        WaitingList.WaitingListProcess dataProcess = new WaitingList.WaitingListProcess() {
            @Override
            public void process(List<WaitingItem> waitingItemList) {
                if (allowToDisplay(displayHash)) {
                    waitingListAdapter.addData(waitingItemList);
                    if (waitingItemList.isEmpty()) {
                        if (reCreate && isSearchWithName) {
                            showToastNotFound();
                        }
                    } else {
                        if (isSearchWithName) {
                            waitingListAdapter.setStartAtName(
                                    waitingItemList.get(waitingItemList.size() - 1).getName());
                        } else {
                            waitingListAdapter.setStartAtTime(
                                    waitingItemList.get(waitingItemList.size() - 1).getTimestamp());
                        }
                    }
                    hideLoadingAnim();
                    if (reCreate) {
                        binding.friendsListRecycleLv.setAdapter(waitingListAdapter);
                    }
                }

            }
        };
        if (!isSearchWithName) {
            waitingList.getListOrderByTime(waitingListAdapter.getStartAtTime(), limit, dataProcess);
        } else {
            if (reCreate) waitingListAdapter.setStartAtName(keywords);
            waitingList.getListSearchByName(keywords,
                    waitingListAdapter.getStartAtName(), limit, dataProcess);
        }
    }

    public void loadAllUsers(boolean reCreate) {
        if (usersListAdapter == null || reCreate) {
            usersListAdapter = new UsersListAdapter(getContext());
            usersListAdapter.setLastNameDisplay(keywords);
            binding.friendsListRecycleLv.setAdapter(usersListAdapter);

            if (!isSearchWithName) {
                hideLoadingAnim();
                return;
            }
        }
        UsersCollection usersCollection = new UsersCollection();
        String displayHash = getDisplayHash();
        int limit = 10;
        usersCollection.findUsersByName(
                keywords, limit, usersListAdapter.getLastNameDisplay(),
                new UsersCollection.FindUserCb() {

                    @Override
                    public void process(List<UserItem> userItemList) {
                        Log.d("user list size " + keywords, "" + userItemList.size());
                        if (allowToDisplay(displayHash)) {
                            usersListAdapter.addData(userItemList);
                            if (userItemList.isEmpty()) {
                                if (reCreate && isSearchWithName) {
                                    showToastNotFound();
                                }
                            } else {
                                usersListAdapter.setLastNameDisplay(
                                        userItemList.get(userItemList.size() - 1).getName());
                            }
                            hideLoadingAnim();
                            if (reCreate) {
                                binding.friendsListRecycleLv.setAdapter(usersListAdapter);

                            }
                        }
                    }
                });

    }

    private void showToastNotFound() {
        Toast.makeText(getContext(),
                getString(R.string.friend_list_no_user_found)
                , Toast.LENGTH_LONG).show();
    }
}