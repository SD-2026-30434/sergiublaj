INSERT INTO CHEFS (ID, NAME, EMAIL, BIRTH_DATE, RATING)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Gordon Ramsay', 'gordon@chefmgmt.com', '1966-11-08T09:00:00Z', 4.9),
    ('22222222-2222-2222-2222-222222222222', 'Massimo Bottura', 'massimo@chefmgmt.com', '1962-09-30T09:00:00Z', 4.8),
    ('33333333-3333-3333-3333-333333333333', 'Ana Ros', 'ana@chefmgmt.com', '1972-11-30T09:00:00Z', 4.7)
ON CONFLICT (ID) DO NOTHING;

INSERT INTO ORDERS (ID, ITEM_NAME, TOTAL_PRICE, ORDERED_AT, CHEF_ID)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Beef Wellington', 89.90, '2026-03-01T12:00:00Z', '11111111-1111-1111-1111-111111111111'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'Truffle Risotto', 54.50, '2026-03-02T13:15:00Z', '22222222-2222-2222-2222-222222222222'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'Sea Bass', 61.00, '2026-03-03T18:40:00Z', '11111111-1111-1111-1111-111111111111'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'Lemon Tart', 22.75, '2026-03-04T19:05:00Z', '33333333-3333-3333-3333-333333333333')
ON CONFLICT (ID) DO NOTHING;
