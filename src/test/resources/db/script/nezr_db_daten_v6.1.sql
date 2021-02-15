INSERT INTO answer VALUES
(NULL, 'ja'), (NULL, 'nein'), (NULL, 'männlich'), (NULL, 'weiblich'), (NULL, '0-14 Jahre'),
(NULL, '15-29 Jahre'), (NULL, '30-49 Jahre'), (NULL, '50-65 Jahre'), (NULL, '66+Jahre'),
(NULL, 'Tagesausflug'), (NULL, 'mehrtägige Reise/ Urlaub'), (NULL, 'bis 50 km'), (NULL, 'bis 100 km'), (NULL, 'bis 200 km'),
(NULL, 'über 200 km'), (NULL, 'Öffentliche Verkehrsmittel'), (NULL, 'Naturerbe Prora Express'), (NULL, 'zu Fuss/per Fahrrad'), (NULL, 'Eigenes KfZ'),
(NULL, 'Reisebus'), (NULL, 'Internet'), (NULL, 'Homepage Naturerbe Zentrum'), (NULL, 'Messe'), (NULL, 'Radio-Werbespot'),
(NULL, 'Radiobericht'), (NULL, 'TV-Bericht'), (NULL, 'Flyer'), (NULL, 'Freunde, Bekannte'), (NULL, 'Tageszeitungsartikel'),
(NULL, '0'),
(NULL, '1'), (NULL, '2'), (NULL, '3'), (NULL, '4'), (NULL, '5'),
(NULL, '6'), (NULL, '7'), (NULL, '8'), (NULL, '9'), (NULL, '10'),
(NULL, '#####'), (NULL, 'Baumkronenpfad Beelitz'), (NULL, 'Baumkronenpfad Haunich'), (NULL, 'Baumkronenpfad Hoherodskopf'), (NULL, 'Baumkronenweg Edersee'),
(NULL, 'Baumkronenweg Füssen'), (NULL, 'Baumkronenweg Waldkirch'), (NULL, 'Baumwipfelpfad Bad Wildbad'), (NULL, 'Baumwipfelpfad Fischbach'), (NULL, 'Baumwipfelpfad Harz'),
(NULL, 'Baumwipfelpfad Neuschönau'), (NULL, 'Baumwipfelpfad Panarbora'), (NULL, 'Baumwipfelpfad Rügen'), (NULL, 'Baumwipfelpfad Saarschleife'), (NULL, 'Baumwipfelpfad Steigerwald'),
(NULL, 'Skywalk Allgäu'), (NULL, 'Waldwipfelweg St. Englmar'), (NULL, 'Baumkronenweg Kopfing'), (NULL, 'Baumwipfelpfad Klopeiner See'), (NULL, 'Baumwipfelweg Althodis'),
(NULL, 'Garten Tulln Baumwipfelweg'), (NULL, 'Glemmtaler Baumwipfelweg'), (NULL, 'Baumwipfelpfad Lipno(CZ)');

INSERT INTO category VALUES
(NULL, 'A1'), (NULL, 'A2'), (NULL, 'A3'), (NULL, 'A4'), (NULL, 'A5'), (NULL, 'A6'), (NULL, 'A7'), 
(NULL, 'B1'), (NULL, 'B2'), (NULL, 'B3'), (NULL, 'B4'),
(NULL, 'C1'), (NULL, 'C2'), (NULL, 'C3'), (NULL, 'D1');

INSERT INTO headline VALUES
(NULL, 'Wohnort'), (NULL, 'Wie bewerten Sie das Ausflugsziel Baumwipfelpfad?');

INSERT INTO multiple_choice VALUES
(NULL, 'Geschlecht', 1, NULL), (NULL, 'Sind Sie mit Kindern/Enkeln hier?', 2, NULL), (NULL, 'Alter', 3, NULL), (NULL, 'Handicap', 3, NULL), (NULL, 'Urlaubsart', 4, NULL), 
(NULL, 'Entfernung von hier bis zum Wohnort', 6, 1), 
(NULL, 'Anreise zum Naturerbe Zentrum', 7, NULL), (NULL, 'Wie wurden Sie auf das Naturerbe Zentrum Rügen aufmerksam?', 8, NULL), (NULL, 'Waren Sie schon einmal im NEZR?', 9, NULL), 
(NULL, 'Waren Sie schon mal auf einem anderen Baumwipfelpfad?', 10, NULL), (NULL, 'Kennen Sie noch andere (weitere) Baumwipfelpfade?', 11, NULL), (NULL, 'Aussicht', 12, 2), (NULL, 'Erlebnisstationen', 12, 2), 
(NULL, 'Informationsstationen', 12, 2), (NULL, 'Bauweise Turm/Pfad', 12, 2), (NULL, 'Barrierefreiheit', 12, 2), (NULL, 'Bezug zur Natur', 12, 2), (NULL, 'Preis-Leistungsverhältnis', 12, 2), 
(NULL, 'Parkplatzsituation', 12, 2), (NULL, 'Dauerausstellung', 12, 2), (NULL, 'Wechselausstellung', 12, 2), (NULL, 'Gastronomie', 12, 2), (NULL, 'Shop', 12, 2), 
(NULL, 'Gesamtbewertung Naturerbe Zentrum Rügen', 12, 2), (NULL, 'Werden Sie das Naturerbe Zentrum Rügen ein weiteres Mal besuchen?', 13, NULL), 
(NULL, 'Werden Sie in Zukunft einen anderen Baumwipfelpfad dieser Art besuchen?', 14, NULL), (NULL, 'wenn ja: wo?', 10, NULL), (NULL, 'wenn ja: welche(n)?', 11, NULL);

INSERT INTO location VALUES
(NULL, 'Bayerischer Wald'), (NULL, 'Rügen'), (NULL, 'Saarschleife'), (NULL, 'Schwarzwald'), (NULL, 'Lipno');

INSERT INTO short_answer VALUES
(NULL, 'Urlaubsort:', 4, NULL),(NULL, 'Land (wenn nicht D):', 5, 1), (NULL, 'Postleitzahl:', 5, 1), (NULL, 'Sonstiges:', 8, NULL), (NULL, 'wenn ja: wie oft?', 9, NULL),
(NULL, 'Anregungen/Bemerkungen', 15, NULL);

INSERT INTO questionnaire VALUES(NULL, '2016-08-01', 'Besucherumfrage', TRUE, 2, FALSE);

INSERT INTO q_has_mc VALUES
(NULL, 1, 1, 1, '+'), (NULL, 1, 2, 2, 'JN +'), (NULL, 1, 3, 3, '+'), (NULL, 1, 4, 3, 'JN X'), (NULL, 1, 5, 4, ''), 
(NULL, 1, 6, 6, ''), (NULL, 1, 7, 7, ''), (NULL, 1, 8, 8, '*'), (NULL, 1, 9, 9, 'JN'), (NULL, 1, 10, 10, 'JN'), (NULL, 1, 11, 11, 'JN'), 
(NULL, 1, 12, 12, 'B'), (NULL, 1, 13, 12, 'B'), (NULL, 1, 14, 12, 'B'), (NULL, 1, 15, 12, 'B'), 
(NULL, 1, 16, 12, 'B'), (NULL, 1, 17, 12, 'B'), (NULL, 1, 18, 12, 'B'), (NULL, 1, 19, 12, 'B'), (NULL, 1, 20, 12, 'B'), 
(NULL, 1, 21, 12, 'B'), (NULL, 1, 22, 12, 'B'), (NULL, 1, 23, 12, 'B'), (NULL, 1, 24, 12, 'B'), (NULL, 1, 25, 13, 'JN'), 
(NULL, 1, 26, 13, 'JN'), (NULL, 1, 27, 10, 'MC10A0 LIST *'), (NULL, 1, 28, 11, 'MC11A0 LIST *');

INSERT INTO q_has_sa VALUES
(NULL, 1, 1, 4, 'MC5A1'), (NULL, 1, 2, 5, 'FF2A0'), (NULL, 1, 3, 5, 'FF1A0 INT==5'), (NULL, 1, 4, 8 , ''), (NULL, 1, 5, 9, 'MC10A0 INT<=5'),
(NULL, 1, 6, 14, 'TEXT');

INSERT INTO mc_has_a VALUES
(NULL, 1, 3), (NULL, 1, 4), 
(NULL, 2, 1), (NULL, 2, 2), 
(NULL, 3, 5), (NULL, 3, 6), (NULL, 3, 7), (NULL, 3, 8), (NULL, 3, 9),
(NULL, 4, 1), (NULL, 4, 2), 
(NULL, 5, 10), (NULL, 5, 11),
(NULL, 6, 12), (NULL, 6, 13), (NULL, 6, 14), (NULL, 6, 15),
(NULL, 7, 16), (NULL, 7, 17),(NULL, 7, 18), (NULL, 7, 19), (NULL, 7, 20), 
(NULL, 8, 21), (NULL, 8, 22), (NULL, 8, 23), (NULL, 8, 24), (NULL, 8, 25), (NULL, 8, 26), (NULL, 8, 27), (NULL, 8, 28), (NULL, 8, 29), 
(NULL, 9, 1), (NULL, 9, 2), 
(NULL, 10, 1), (NULL, 10, 2), 
(NULL, 11, 1), (NULL, 11, 2), 
(NULL, 12, 31), (NULL, 12, 32), (NULL, 12, 33), (NULL, 12, 34), (NULL, 12, 35), 
(NULL, 12, 36), (NULL, 12, 37), (NULL, 12, 38), (NULL, 12, 39), (NULL, 12, 40),
(NULL, 12, 30), 
(NULL, 13, 31), (NULL, 13, 32), (NULL, 13, 33), (NULL, 13, 34), (NULL, 13, 35), 
(NULL, 13, 36), (NULL, 13, 37), (NULL, 13, 38), (NULL, 13, 39), (NULL, 13, 40),
(NULL, 13, 30), 
(NULL, 14, 31), (NULL, 14, 32), (NULL, 14, 33), (NULL, 14, 34), (NULL, 14, 35), 
(NULL, 14, 36), (NULL, 14, 37), (NULL, 14, 38), (NULL, 14, 39), (NULL, 14, 40),
(NULL, 14, 30), 
(NULL, 15, 31), (NULL, 15, 32), (NULL, 15, 33), (NULL, 15, 34), (NULL, 15, 35), 
(NULL, 15, 36), (NULL, 15, 37), (NULL, 15, 38), (NULL, 15, 39), (NULL, 15, 40),
(NULL, 15, 30), 
(NULL, 16, 31), (NULL, 16, 32), (NULL, 16, 33), (NULL, 16, 34), (NULL, 16, 35), 
(NULL, 16, 36), (NULL, 16, 37), (NULL, 16, 38), (NULL, 16, 39), (NULL, 16, 40),
(NULL, 16, 30), 
(NULL, 17, 31), (NULL, 17, 32), (NULL, 17, 33), (NULL, 17, 34), (NULL, 17, 35), 
(NULL, 17, 36), (NULL, 17, 37), (NULL, 17, 38), (NULL, 17, 39), (NULL, 17, 40),
(NULL, 17, 30), 
(NULL, 18, 31), (NULL, 18, 32), (NULL, 18, 33), (NULL, 18, 34), (NULL, 18, 35), 
(NULL, 18, 36), (NULL, 18, 37), (NULL, 18, 38), (NULL, 18, 39), (NULL, 18, 40),
(NULL, 18, 30), 
(NULL, 19, 31), (NULL, 19, 32), (NULL, 19, 33), (NULL, 19, 34), (NULL, 19, 35), 
(NULL, 19, 36), (NULL, 19, 37), (NULL, 19, 38), (NULL, 19, 39), (NULL, 19, 40),
(NULL, 19, 30), 
(NULL, 20, 31), (NULL, 20, 32), (NULL, 20, 33), (NULL, 20, 34), (NULL, 20, 35), 
(NULL, 20, 36), (NULL, 20, 37), (NULL, 20, 38), (NULL, 20, 39), (NULL, 20, 40),
(NULL, 20, 30), 
(NULL, 21, 31), (NULL, 21, 32), (NULL, 21, 33), (NULL, 21, 34), (NULL, 21, 35), 
(NULL, 21, 36), (NULL, 21, 37), (NULL, 21, 38), (NULL, 21, 39), (NULL, 21, 40),
(NULL, 21, 30), 
(NULL, 22, 31), (NULL, 22, 32), (NULL, 22, 33), (NULL, 22, 34), (NULL, 22, 35), 
(NULL, 22, 36), (NULL, 22, 37), (NULL, 22, 38), (NULL, 22, 39), (NULL, 22, 40),
(NULL, 22, 30), 
(NULL, 23, 31), (NULL, 23, 32), (NULL, 23, 33), (NULL, 23, 34), (NULL, 23, 35), 
(NULL, 23, 36), (NULL, 23, 37), (NULL, 23, 38), (NULL, 23, 39), (NULL, 23, 40),
(NULL, 23, 30), 
(NULL, 24, 31), (NULL, 24, 32), (NULL, 24, 33), (NULL, 24, 34), (NULL, 24, 35), 
(NULL, 24, 36), (NULL, 24, 37), (NULL, 24, 38), (NULL, 24, 39), (NULL, 24, 40),
(NULL, 24, 30), 
(NULL, 25, 1), (NULL, 25, 2), 
(NULL, 26, 1), (NULL, 26, 2), 
(NULL, 27, 42), (NULL, 27, 43), (NULL, 27, 44), (NULL, 27, 45), (NULL, 27, 46), 
(NULL, 27, 47), (NULL, 27, 48), (NULL, 27, 49), (NULL, 27, 50), (NULL, 27, 51), 
(NULL, 27, 52), (NULL, 27, 53), (NULL, 27, 54), (NULL, 27, 55), (NULL, 27, 56), 
(NULL, 27, 57), (NULL, 27, 58), (NULL, 27, 59), (NULL, 27, 60), (NULL, 27, 61), 
(NULL, 27, 62), (NULL, 27, 63), 
(NULL, 28, 42), (NULL, 28, 43), (NULL, 28, 44), (NULL, 28, 45), (NULL, 28, 46), 
(NULL, 28, 47), (NULL, 28, 48), (NULL, 28, 49), (NULL, 28, 50), (NULL, 28, 51), 
(NULL, 28, 52), (NULL, 28, 53), (NULL, 28, 54), (NULL, 28, 55), (NULL, 28, 56), 
(NULL, 28, 57), (NULL, 28, 58), (NULL, 28, 59), (NULL, 28, 60), (NULL, 28, 61), 
(NULL, 28, 62), (NULL, 28, 63);