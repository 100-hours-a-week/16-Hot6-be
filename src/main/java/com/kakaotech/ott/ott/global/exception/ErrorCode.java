package com.kakaotech.ott.ott.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Token
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 카카오 access token입니다."),

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh token입니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "Refresh token이 누락되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다. 다시 로그인해 주세요."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    USER_DELETED(HttpStatus.FORBIDDEN, "탈퇴한 사용자입니다."),
    LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "로그아웃 처리에 실패했습니다."),
    USER_ALREADY_AUTHENTICATED(HttpStatus.FORBIDDEN, "이미 추천인 코드를 등록하셨습니다."),
    USER_NOT_VERIFIED(HttpStatus.FORBIDDEN, "카테부 회원만 이용 가능합니다."),
    INVALID_INPUT_CODE(HttpStatus.BAD_REQUEST, "추천인 코드가 일치하지 않습니다."),
    DUPLICATE_NICKNAME_COMMUNITY(HttpStatus.CONFLICT, "이미 사용 중인 커뮤니티 닉네임입니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 사용자의 권한이 없습니다."),
    USER_NOT_ADMIN(HttpStatus.FORBIDDEN, "일반 사용자는 이용하실 수 없습니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."),
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, "유효하지 않은 lastPostId 값입니다."),

    // AiImage
    QUOTA_ALREADY_USED(HttpStatus.FORBIDDEN, "금일 이미지 생성 할당량을 모두 사용했습니다."),
    AIIMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 AI 이미지가 존재하지 않습니다."),
    AI_IMAGE_ALREADY_USED(HttpStatus.CONFLICT, "해당 AI 이미지는 이미 게시물에 연결되어 있습니다."),
    INVALID_IMAGE(HttpStatus.BAD_REQUEST, "올바른 데스크 이미지가 아닙니다."),
    FAILED_GENERATING_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "AI 서버에서 이미지 생성에 실패했습니다."),

    // Product
    DESK_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 데스크 추천 상품이 존재하지 않습니다."),
    AI_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 AI 이미지에 대한 추천 상품이 존재하지 않습니다."),

    // Scrap
    SCRAP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 스크랩한 게시글입니다."),
    SCRAP_NOT_FOUND(HttpStatus.CONFLICT, "스크랩하지 않은 게시글입니다."),

    // Like
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요한 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.CONFLICT, "좋아요하지 않은 게시글입니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다."),

    // REPLY
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 대댓글이 존재하지 않습니다."),

    // PRODUCTORDER
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문이 존재하지 않습니다."),

    NOT_PENDING_STATE(HttpStatus.BAD_REQUEST, "결제 대기 상태가 아닙니다."),
    NOT_PAID_STATE(HttpStatus.BAD_REQUEST, "결제 완료 상태가 아닙니다."),
    NOT_DELIVERED_STATE(HttpStatus.BAD_REQUEST, "배송 완료된 상태가 아닙니다."),
    NOT_CONFIRMED_STATE(HttpStatus.BAD_REQUEST, "주문 확정된 상태가 아닙니다."),


    ALREADY_ORDERED(HttpStatus.BAD_REQUEST, "이미 주문된 상태입니다."),
    ALREADY_PAID(HttpStatus.BAD_REQUEST, "이미 결제된 주문입니다."),
    ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "이미 확정된 주문입니다."),
    ALREADY_DELIVERED(HttpStatus.BAD_REQUEST, "이미 배송된 주문입니다."),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 주문내역입니다."),

    // product
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "이미지 처리 중 오류가 발생했습니다."),
    VARIANT_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "사진 정보는 필수입니다."),
    DUPLICATE_PRODUCT_NAME(HttpStatus.CONFLICT, "이미 존재하는 상품명입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "품목을 찾을 수 없습니다."),
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "특가 정보를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 상태입니다."),
    INVALID_DISCOUNT(HttpStatus.BAD_REQUEST, "할인가는 정가보다 클 수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    INVALID_VARIANT_STATUS(HttpStatus.BAD_REQUEST, "품목 상태가 올바르지 않습니다."),

    // ORDERITEM
    NOT_ORDERED_ITEM_STATE(HttpStatus.BAD_REQUEST, "주문 대기 상태가 아닙니다."),
    NOT_PAID_ITEM_STATE(HttpStatus.BAD_REQUEST, "결제 완료 상태가 아닙니다."),
    NOT_REFUNDABLE_STATE(HttpStatus.BAD_REQUEST, "환불 가능한 상태가 아닙니다."),
    NOT_CANCELABLE_STATE(HttpStatus.BAD_REQUEST, "주문 취소 가능한 상태가 아닙니다."),
    NOT_CONFIRMABLE_STATE(HttpStatus.BAD_REQUEST, "주문 확정 가능한 상태가 아닙니다."),

    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문 상품이 존재하지 않습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "유효하지 않은 값을 입력했습니다."),

    // POINTHISOTRY
    POINT_HISTORY_NOT_FOUNR(HttpStatus.BAD_REQUEST, "포인트 내역이 존재하지 않습니다."),
    INSUFFICIENT_POINT_BALANCE(HttpStatus.BAD_REQUEST, "보유 포인트가 부족합니다."),
    PAYMENT_POINT_BALANCE(HttpStatus.BAD_REQUEST, "결제 금액이 부족합니다."),

    // PAYMENT
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "결제한 내역이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
