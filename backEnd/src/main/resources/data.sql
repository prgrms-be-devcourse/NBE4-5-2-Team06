-- 기존 데이터 삭제 (데이터 초기화)
DELETE FROM WINNER_TABLE;
DELETE FROM BID_TABLE;
DELETE FROM AUCTION_TABLE;
DELETE FROM PRODUCT_TABLE;
DELETE FROM USER_TABLE;

ALTER TABLE USER_TABLE ALTER COLUMN PROFILE_IMAGE TEXT;

-- USER 테이블
INSERT INTO USER_TABLE (USER_UUID, EMAIL, NICKNAME, PASSWORD, CREATED_DATE, MODIFIED_AT, ROLE)
VALUES
    ('user1', 'user1@example.com', 'AuctionMaster', 'password123', '1990-01-01', '2025-03-05', 'USER'),
    ('user2', 'user2@example.com', 'BidKing', 'password456', '1985-06-15', '2025-03-05', 'USER'),
    ('admin1', 'admin@example.com', 'AdminUser', 'adminpassword', '1980-12-30', '2025-03-05', 'ADMIN');


-- PRODUCT 테이블
INSERT INTO PRODUCT_TABLE (PRODUCT_ID, PRODUCT_NAME, IMAGE_URL, DESCRIPTION)
VALUES
    (1, 'Apple MacBook Pro', 'https://store.storeimages.cdn-apple.com/8756/as-images.apple.com/is/macbook-pro-og-202410?wid=600&hei=315&fmt=jpeg&qlt=95&.v=1728658184478
', 'Apple의 최신 MacBook Pro 모델입니다.'),
    (2, 'Vintage Watch', 'https://sitem.ssgcdn.com/16/72/26/item/1000598267216_i1_750.jpg', '고급 빈티지 시계.'),
    (3, 'Sony 4K TV', 'https://m.media-amazon.com/images/I/71TGwbLjzAL.jpg', '최신 4K 해상도 Sony TV.');


-- AUCTION 테이블
INSERT INTO AUCTION_TABLE (AUCTION_ID, PRODUCT_ID, START_PRICE, MIN_BID, START_TIME, END_TIME, STATUS)
VALUES
    (1, 1, 1000000, 5000, '2025-03-15 12:00:00', '2025-03-15 20:00:00', 'ONGOING'),
    (2, 2, 300000, 10000, '2025-03-16 14:00:00', '2025-03-16 20:00:00', 'UPCOMING'),
    (3, 3, 500000, 25000, '2025-03-03 16:00:00', '2025-03-16 18:00:00', 'FINISHED');


-- BID 테이블
INSERT INTO BID_TABLE (BID_ID, AUCTION_ID, USER_UUID, AMOUNT, BID_TIME)
VALUES
    (1, 1, 'user1', 1050000, '2025-03-01 12:30:00'),
    (2, 1, 'user2', 1100000, '2025-03-02 13:00:00'),
    (3, 2, 'user1', 320000, '2025-03-02 15:00:00');

--
-- -- -- WINNER 테이블
--      INSERT INTO WINNER_TABLE (WINNER_ID, USER_UUID, AUCTION_ID, WINNING_BID, WIN_TIME)
--      VALUES
--      (1, 'user2', 1, 1100000, '2025-03-02 13:30:00'),
--      (2, 'user1', 3, 550000, '2025-03-13 17:00:00');