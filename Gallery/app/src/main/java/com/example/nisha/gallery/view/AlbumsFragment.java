package com.example.nisha.gallery.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nisha.gallery.R;
import com.example.nisha.gallery.adapter.AlbumsListAdapter;
import com.example.nisha.gallery.data.Album;
import com.example.nisha.gallery.data.source.AlbumDetailsIntractor;
import com.example.nisha.gallery.presenter.AlbumDetailPresenter;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlbumsFragment extends Fragment implements AlbumsContract.View {

    private AlbumsContract.Presenter mPresenter;
    private RecyclerView mAlbumRecyclerView;
    private AlbumsListAdapter mAlbumsListAdapter;
    private List<Album> albums = new ArrayList<>();
    private AlbumsListAdapter.AlbumItemListener mAlbumItemListener;
    private AlbumDetailPresenter mAlbumDetailPresenter;

    public AlbumsFragment() {
    }

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbumItemListener = new AlbumsListAdapter.AlbumItemListener() {
            @Override
            public void onAlbumClicked(Album album) {
                mPresenter.navigateToAlbumDetails(album);
            }
        };
        mAlbumsListAdapter = new AlbumsListAdapter(albums, mAlbumItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_album, container, false);
        mAlbumRecyclerView = (RecyclerView) root.findViewById(R.id.album_list);
        mAlbumRecyclerView.setAdapter(mAlbumsListAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.app_name));
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull AlbumsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showAlbums(List<Album> albums) {
        this.albums = albums;
        mAlbumsListAdapter.replaceData(albums);
    }

    @Override
    public void showAlbumDetailsUI(Album album) {
        AlbumDetailFragment albumDetailFragment = AlbumDetailFragment.newInstance();
        mAlbumDetailPresenter = new AlbumDetailPresenter(album.getId(), albumDetailFragment, new AlbumDetailsIntractor());
        Bundle newBundle = new Bundle();
        newBundle.putString("AlbumID", album.getId());
        newBundle.putString("AlbumTitle", album.getTitle());
        albumDetailFragment.setArguments(newBundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, albumDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
