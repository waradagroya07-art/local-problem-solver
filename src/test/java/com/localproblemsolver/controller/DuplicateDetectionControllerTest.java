package com.localproblemsolver.controller;

import com.localproblemsolver.dto.DuplicateResponse;
import com.localproblemsolver.service.DuplicateDetectionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DuplicateDetectionControllerTest {

    private final DuplicateDetectionService service =
            mock(DuplicateDetectionService.class);
    private final DuplicateDetectionController controller =
            new DuplicateDetectionController(service);

    @Test
    void findDuplicates_delegatesToService() {
        List<DuplicateResponse> expected =
                List.of(mock(DuplicateResponse.class));

        when(service.findDuplicates(10L))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.findDuplicates(10L)
        );

        verify(service).findDuplicates(10L);
    }
}
