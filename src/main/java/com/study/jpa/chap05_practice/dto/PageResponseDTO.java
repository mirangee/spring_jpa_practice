package com.study.jpa.chap05_practice.dto;


import com.study.jpa.chap05_practice.entity.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter @Setter @ToString
public class PageResponseDTO {

    private int startPage;
    private int endPage;
    private int currentPage;

    private boolean prev, next;

    private int totalCount;

    // 한 페이지에 배치할 페이지 버튼 수 ( 1~ 10 // 11~ 20 // ...)
    private static final int PAGE_COUNT = 10;

    public PageResponseDTO(Page<Post> pageData) {
        // 기존에 사용했던 PageCreator랑 다를 게 없다.
        // 매개값으로 전달된 Page 객체가 기존보다 많은 정보를 제공하기 때문에
        // 로직이 좀 더 간편해진 것 뿐이다.
        this.totalCount = (int) pageData.getTotalElements();
        this.currentPage = pageData.getPageable().getPageNumber() + 1;
        this.endPage = (int) (Math.ceil((double) currentPage/PAGE_COUNT) * PAGE_COUNT);
        this.startPage = endPage - PAGE_COUNT + 1;
        long realEnd = pageData.getTotalElements();
        if(realEnd < this.endPage) this.endPage = (int) realEnd;
        this.prev = startPage > 1;
        this.next = endPage < realEnd;
    }
}
