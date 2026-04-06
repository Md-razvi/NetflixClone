package com.netflix.clone.util;
import com.netflix.clone.entity.User;
import com.netflix.clone.entity.Video;
import com.netflix.clone.exceptions.ResourceNotFoundException;
import com.netflix.clone.repository.VideoRepository;
import org.springframework.stereotype.Component;
import com.netflix.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.clone.dto.request.UserRequest;

@Component
public class ServiceUtil {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;
    public User getByEmailOrThrow(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found with email:"+email));
    }
    public User getUserByIdOrThrow(Long Id){
        return userRepository.findById(Id).orElseThrow(()->new ResourceNotFoundException("User not found with ID:"+Id));
    }
    public Video getVideoByIdOrThrow(Long Id){
        return videoRepository.findById(Id).orElseThrow(()->new ResourceNotFoundException("Video not found with ID:"+Id));
    }
}
