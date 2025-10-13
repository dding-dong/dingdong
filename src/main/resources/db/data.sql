INSERT INTO p_store_category (id, name, description, image_url)
VALUES ('20227a32-fa45-4d87-a972-27f32b475024'::uuid, '한식', '한식 전문점', 'https://image.url/korean.jpg');

INSERT INTO p_store (id, owner_user_id, store_category_id, name, image_url, address, postal_code,
                     min_order_price, status, opened_at, closed_at, day_of_week, created_at, updated_at)
VALUES ('246fdbcd-3258-4fd7-9353-38a4ac5ee2ff'::uuid, -- 고정 UUID 예시
        2, -- 점주 User UUID
        '20227a32-fa45-4d87-a972-27f32b475024'::uuid, -- 카테고리 UUID
        '맛있는 치킨집',
        'https://example.com/chicken.jpg',
        '서울시 강남구 테헤란로 123',
        '06234',
        20000,
        'OPEN',
        '2025-10-01 10:00:00',
        '2025-10-01 22:00:00',
        'MON,TUE,WED,THU,FRI',
        NOW(),
        NOW());


INSERT INTO p_menu_item (id, created_at, created_by, deleted_at, deleted_by, updated_at, updated_by,
                         ai_content, content, image_url, images,
                         is_active, is_ai_used, is_displayed, is_recommended, is_soldout,
                         name, price, store_id)
VALUES ('96e49a63-3f80-4360-83c6-721c1ffc4a06', '2025-10-05 15:22:33.993033', 1, NULL, NULL, NULL, NULL,
        '국물이 진하고 깊은 김치찌개', NULL, NULL, NULL,
        TRUE, FALSE, TRUE, FALSE, FALSE,
        '김치찌개', 8000, '246fdbcd-3258-4fd7-9353-38a4ac5ee2ff'),

       ('f88eef64-a84a-450b-a797-3c4d255e68bb', '2025-10-05 15:22:33.993033', 1, NULL, NULL, NULL, NULL,
        '구수한 된장찌개', NULL, NULL, NULL,
        TRUE, FALSE, TRUE, FALSE, FALSE,
        '된장찌개', 7500, '246fdbcd-3258-4fd7-9353-38a4ac5ee2ff'),

       ('697e6efb-19b0-4566-a2cd-de11ffc23d33', '2025-10-05 15:22:33.993033', 2, NULL, NULL, NULL, NULL,
        '달콤하고 진한 짜장면', NULL, NULL, NULL,
        TRUE, FALSE, TRUE, TRUE, FALSE,
        '짜장면', 7000, '246fdbcd-3258-4fd7-9353-38a4ac5ee2ff'),

       ('e705721d-ec2f-4096-8e5b-cb12a64dd70c', '2025-10-05 15:22:33.993033', 2, NULL, NULL, NULL, NULL,
        '얼큰하고 시원한 짬뽕', NULL, NULL, NULL,
        TRUE, FALSE, TRUE, TRUE, FALSE,
        '짬뽕', 8500, '246fdbcd-3258-4fd7-9353-38a4ac5ee2ff');


INSERT INTO p_cart (id, user_id, store_id, created_at, created_by)
VALUES ('a1f4d2e0-1234-4a77-9a8a-7d6e53c3f3a1'::uuid, 1, '246fdbcd-3258-4fd7-9353-38a4ac5ee2ff'::uuid, NOW(), 3);


INSERT INTO p_cart_item (id, cart_id, menu_item_id, quantity, unit_price, created_at, created_by)
VALUES
    ('b2a6c9d1-4321-48f5-bc4b-0e28c6f01a55'::uuid, 'a1f4d2e0-1234-4a77-9a8a-7d6e53c3f3a1'::uuid, '96e49a63-3f80-4360-83c6-721c1ffc4a06'::uuid, 2, 8000, NOW(), 3),
    ('e7a8c2f9-99d2-44cd-9e21-8fc247fda944'::uuid, 'a1f4d2e0-1234-4a77-9a8a-7d6e53c3f3a1'::uuid, 'e705721d-ec2f-4096-8e5b-cb12a64dd70c'::uuid, 1, 8500, NOW(), 3);


INSERT INTO p_order (
    id,
    user_id,
    store_id,
    status,
    total_price,
    delivery_address,
    placed_at,
    created_at,
    created_by
)
VALUES (
           'f7a9d21c-4c3b-4c42-a8a1-1bbf4f56ccf1'::uuid,
           1,
           '246fdbcd-3258-4fd7-9353-38a4ac5ee2ff'::uuid,
           0,
           24500,
           '서울특별시 강남구 테헤란로 123',
           NOW(),
           NOW(),
           3
       );

INSERT INTO p_order_item (
    id,
    order_id,
    menu_item_id,
    unit_price,
    quantity,
    created_at,
    created_by
)
VALUES
    ('1a2b3c4d-1111-4d3c-8a77-3f1c5f9eaaa1'::uuid,
     'f7a9d21c-4c3b-4c42-a8a1-1bbf4f56ccf1'::uuid,
     '96e49a63-3f80-4360-83c6-721c1ffc4a06'::uuid,
     8000,
     2,
     NOW(),
     3),

    ('2b3c4d5e-2222-4d3c-8a77-3f1c5f9eaaa2'::uuid,
     'f7a9d21c-4c3b-4c42-a8a1-1bbf4f56ccf1'::uuid,
     'e705721d-ec2f-4096-8e5b-cb12a64dd70c'::uuid,
     8500,
     1,
     NOW(),
     3);