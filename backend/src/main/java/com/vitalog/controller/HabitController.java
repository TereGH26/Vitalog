package com.vitalog.controller;

import com.vitalog.dto.CreateHabitRequest;
import com.vitalog.dto.HabitResponse;
import com.vitalog.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public List<HabitResponse> list(@AuthenticationPrincipal UserDetails userDetails) {
        return habitService.findByAccount(userDetails.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitResponse create(
            @Valid @RequestBody CreateHabitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return habitService.create(request, userDetails.getUsername());
    }
}
