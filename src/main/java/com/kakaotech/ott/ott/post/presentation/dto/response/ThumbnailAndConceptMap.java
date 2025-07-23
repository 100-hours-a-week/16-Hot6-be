package com.kakaotech.ott.ott.post.presentation.dto.response;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;

import java.util.Map;

public class ThumbnailAndConceptMap {
    private Map<Long, String> thumbnailMap;
    private Map<Long, AiImageConcept> conceptMap;

    public ThumbnailAndConceptMap(Map<Long, String> thumbnailMap, Map<Long, AiImageConcept> conceptMap) {
        this.thumbnailMap = thumbnailMap;
        this.conceptMap = conceptMap;
    }

    public Map<Long, String> getThumbnailMap() {
        return thumbnailMap;
    }

    public Map<Long, AiImageConcept> getConceptMap() {
        return conceptMap;
    }
}
