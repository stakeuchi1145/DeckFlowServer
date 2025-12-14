INSERT INTO user_cards (user_id, card_id, quantity, location)
SELECT
    (SELECT id FROM users WHERE email = ?),
    (SELECT id FROM cards WHERE name = ? AND pack_id = (SELECT id FROM packs WHERE code = ?) AND number = ?),
    ?,
    ?
ON CONFLICT (user_id, card_id) DO UPDATE SET quantity = excluded.quantity, location = excluded.location
RETURNING id
