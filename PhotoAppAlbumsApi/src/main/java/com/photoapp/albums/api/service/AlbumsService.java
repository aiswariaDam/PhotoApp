package com.photoapp.albums.api.service;

import com.photoapp.albums.api.data.AlbumEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AlbumsService {
    List<AlbumEntity> getAlbums(String userId);

}
