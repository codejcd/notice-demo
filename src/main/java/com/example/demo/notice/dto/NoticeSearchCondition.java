package com.example.demo.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeSearchCondition {

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;


}
