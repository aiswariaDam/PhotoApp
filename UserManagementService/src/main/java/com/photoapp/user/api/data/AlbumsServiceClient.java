package com.photoapp.user.api.data;

import com.photoapp.user.api.model.AlbumResponseModel;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "albums-ws", fallback = AlbumsFallBack.class)
public interface AlbumsServiceClient {

    @GetMapping("/users/{id}/albums")
    public List<AlbumResponseModel> getAlbums(@PathVariable String id);
}

@Component
class AlbumsFallBack implements  AlbumsServiceClient{

    @Override
    public List<AlbumResponseModel> getAlbums(String id) {
        return null;
    }
}