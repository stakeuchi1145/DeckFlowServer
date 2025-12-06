SELECT uc.id,
       c.name      AS card_name,
       c.image_url AS card_image_url,
       p.name      AS pack_name,
       uc.quantity,
       CASE
           WHEN EXISTS (SELECT 1
                        FROM regulation_allowed_marks ram
                                 JOIN regulations r ON ram.regulation_id = r.id
                        WHERE ram.regulation_mark_id = p.regulation_mark_id
                          AND r.code = 'STANDARD')
               THEN TRUE
           ELSE FALSE
           END     AS is_standard
FROM user_cards uc
         JOIN users u
              ON uc.user_id = u.id
         JOIN cards c
              ON uc.card_id = c.id
         JOIN packs p
              ON c.pack_id = p.id
WHERE u.auth_uid = ?;
