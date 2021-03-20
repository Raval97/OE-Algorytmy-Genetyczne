----------------------------------------------------------
-- ALGORYTM GENETYCZNY
-- 2017.02.01
-- znajduje maksmaln¹ wartoœæ rokladu normalnego 
-- o œredniej u=16 i odchyleniu standardowym sigma = 4 
----------------------------------------------------------

----------------------------------------------------------
-- PAKIET ALGORYTMU GENETYCZNEGO
----------------------------------------------------------

CREATE OR REPLACE PACKAGE gen_pkg
IS
  v_prawdop_krzyz NUMBER(3,2);
  v_prawdop_mutacji NUMBER(5,4);
  PROCEDURE resetuj_dane;
  FUNCTION oblicz_wart_przystosowania(p_x gen_tab_01.chromosom_dziesietnie%TYPE, p_srednia NUMBER := 16, p_odchylenie_std NUMBER := 4) RETURN gen_tab_01.wart_przystosowania%TYPE;
  PROCEDURE wpisz_wart_przystosowania;
  PROCEDURE przeskal_i_wpisz_wart_przystos(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE);  
-- deklaraja dodanych procedur i funkcji  
  PROCEDURE sel_rankingowa(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE);  
  FUNCTION sel_turniejowa( p_pierwsze_id gen_tab_01.id%TYPE,  p_drugie_id gen_tab_01.id%TYPE) RETURN gen_tab_01.id%TYPE;
  FUNCTION gray_encoder(chromosom gen_tab_01.chromosom%TYPE) RETURN gen_tab_01.chromosom%TYPE;
  PROCEDURE wpisz_chromoson_encoder_gray(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE);
  FUNCTION gray_decoder(GRAY gen_tab_01.chromosom%TYPE) RETURN gen_tab_01.chromosom%TYPE;
  PROCEDURE wpisz_chromoson_decoder_gray(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE);
-- 
  FUNCTION oblicz_prawdop_selekcji(p_id gen_tab_01.id%TYPE) RETURN gen_tab_01.prawdopod_selekcji%TYPE;
  PROCEDURE wpisz_prawdop_selekcji(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE);
  FUNCTION selekcja(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE, p_pierwsze_id gen_tab_01.id%TYPE := 0, p_drugi_id gen_tab_01.id%TYPE := 0, p_trzeci_id gen_tab_01.id%TYPE := 0 ) RETURN gen_tab_01.id%TYPE;
  FUNCTION bin2dec (binval in char) RETURN number;
  PROCEDURE krzyzowanie_i_wpis(v_chormosom_id_1 gen_tab_01.id%TYPE, v_chormosom_id_2 gen_tab_01.id%TYPE, p_gray VARCHAR2);
  PROCEDURE krzyzowanie_rowno_i_wpis(v_chormosom_id_1 gen_tab_01.id%TYPE, v_chormosom_id_2 gen_tab_01.id%TYPE, p_gray VARCHAR2);  
  PROCEDURE mutacja_i_wpis(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE, p_gray VARCHAR2);
  PROCEDURE algorytm(p_liczba_pokolen NUMBER
                   , p_prawdop_krzyz NUMBER
                   , p_prawdop_mutacji NUMBER
                   , p_sred_przystosowanie_docelowe gen_tab_01.wart_przystosowania%TYPE
                   , p_metoda_selekcji VARCHAR2
                   , p_metoda_krzyzowania VARCHAR2
                   , p_gray VARCHAR2);
END gen_pkg;
/
show err

CREATE OR REPLACE PACKAGE BODY gen_pkg
IS

  PROCEDURE resetuj_dane
  IS
  BEGIN
    EXECUTE IMMEDIATE 'TRUNCATE TABLE gen_tab_01';
    BEGIN
      EXECUTE IMMEDIATE 'DROP SEQUENCE gen_seq_01';
    EXCEPTION
      WHEN OTHERS THEN
         IF SQLCODE = -02289 THEN
           NULL;
         END IF;
    END;
    EXECUTE IMMEDIATE 'CREATE SEQUENCE gen_seq_01';
    INSERT INTO gen_tab_01 SELECT * FROM gen_tab_01_reset;  -- tabela z danymi POCZ¥TKOWYMI
    COMMIT;
  END resetuj_dane;

  FUNCTION oblicz_wart_przystosowania(p_x gen_tab_01.chromosom_dziesietnie%TYPE, p_srednia NUMBER := 16, p_odchylenie_std NUMBER := 4) 
  RETURN gen_tab_01.wart_przystosowania%TYPE
  IS
    v_f_x gen_tab_01.wart_przystosowania%TYPE;
    c_pi NUMBER(15,14) := 3.14159265358979;
    c_e NUMBER(15,14) := 2.71828182845905;
  BEGIN
    v_f_x := round((1/ (sqrt(2*c_pi)*p_odchylenie_std))*power(c_e,(-1/(2*power(p_odchylenie_std,2)))*power(p_x-p_srednia,2)),6);
    RETURN v_f_x;
  END oblicz_wart_przystosowania;

  PROCEDURE wpisz_wart_przystosowania
  IS
  BEGIN
   -- wypisywanie do tabeli wyników WARTOSCI PRZYSTOSOWANIA
    UPDATE gen_tab_01 SET wart_przystosowania = gen_pkg.oblicz_wart_przystosowania(chromosom_dziesietnie)
    WHERE id_pokolenia = 
    (
      SELECT max(id_pokolenia) FROM gen_tab_01
    );
    COMMIT;
  END wpisz_wart_przystosowania;

-- SKALOWANIE TYPU SIGMA
-- PRZESKALOWYWANIE przystosowania chromosomu
-- gdy pojedynczy chromosom domunuje na pocz¹tku dzialania algorytmu
-- to zmiennoœc przystosowania bêdzie du¿a i skalowalnoœæ przez du¿¹ wartoœæ zmiennosci zmniejszy tê dominacjê
-- póŸniej, w dalszych pokoleniach, populacje s¹ ju¿ bardziej jednorodne i skalowalnie bêdzie przez mniejsze wartoœci
-- i dobrze przystosowane chromosomy bêd¹ siê rozmna¿aly
  PROCEDURE przeskal_i_wpisz_wart_przystos(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE)
  IS
  BEGIN
    -- kopiowanie rekordow wraz z PRAWDOPODOBIEÑSTWEM SELEKCJI
    INSERT INTO gen_tab_01_temp 
    SELECT id
         , id_pokolenia
         , chromosom
         , chromosom_dziesietnie
         , 1 + CASE WHEN STDDEV_POP(wart_przystosowania) OVER (PARTITION BY id_pokolenia) = 0 THEN 0
                    ELSE ( ( wart_przystosowania 
                           - AVG(wart_przystosowania) OVER (PARTITION BY id_pokolenia)
                           ) 
                          / STDDEV_POP(wart_przystosowania) OVER (PARTITION BY id_pokolenia)
                         )
                END
         , null
    FROM gen_tab_01
    WHERE id_pokolenia = p_id_pokolenia;
    
    -- wypisywanie do tabeli wyników PRAWDOPODOBIEÑSTWA SELEKCJI
    MERGE INTO gen_tab_01 g
    USING
    (
      SELECT * FROM gen_tab_01_temp 
    ) gt
    ON(g.id = gt.id)
    WHEN MATCHED THEN UPDATE SET
    g.wart_przystosowania = gt.wart_przystosowania;
    COMMIT;
  END przeskal_i_wpisz_wart_przystos;
  
--#############################################################################################################################
--######################################## BEGIN MOJE FUNKCJE I PROcEDURY #####################################################
--#############################################################################################################################
  
--####################################################################
-- SELEKCJA RANKINGOWA
--####################################################################  
  PROCEDURE sel_rankingowa(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE)
  IS
  BEGIN
    INSERT INTO gen_tab_01_temp 
	SELECT  id 
        , id_pokolenia
        , chromosom
        , chromosom_dziesietnie
        , wart_przystosowania
        , pozycja/suma_rankingow
        from ( select  X.*
                , sum(pozycja) over (ORDER BY id_pokolenia) suma_rankingow
                from (select q.*,
                    rank() over (ORDER BY prawdopod_selekcji) POZYCJA	 
                    FROM gen_tab_01 q
                    WHERE id_pokolenia = p_id_pokolenia
                    order by pozycja
                    ) X
                ) XX ;
                
	MERGE INTO gen_tab_01 g
	USING
	(
	  SELECT * FROM gen_tab_01_temp 
	) gt
	ON(g.id = gt.id)
	WHEN MATCHED THEN UPDATE SET
	g.prawdopod_selekcji = gt.prawdopod_selekcji;
	COMMIT;
  END sel_rankingowa;
  
--####################################################################
-- SELEKCJA TURNIEJOWA
--#################################################################### 
  FUNCTION sel_turniejowa( p_pierwsze_id gen_tab_01.id%TYPE,  p_drugie_id gen_tab_01.id%TYPE)
  RETURN gen_tab_01.id%TYPE
  IS
  v_losowa NUMBER(5,2);
  v_prog NUMBER(5,2) :=0.67;
  v_wart_przystosowania_1 gen_tab_01.wart_przystosowania%TYPE;
  v_wart_przystosowania_2 gen_tab_01.wart_przystosowania%TYPE;
  v_rodzic_id gen_tab_01.id%TYPE;
  BEGIN
    -- wartosc losowana z zakresu <0,1>
    v_losowa := dbms_random.value;
    
    -- przypisanie wartoœæi funkcji dopasowania dla wskazanych id chromosomów
    SELECT wart_przystosowania 
    INTO v_wart_przystosowania_1
    FROM gen_tab_01 WHERE id = p_pierwsze_id;
    
    SELECT wart_przystosowania 
    INTO v_wart_przystosowania_2
    FROM gen_tab_01 WHERE id = p_drugie_id;
    
    -- zwrucenie id chromosomu na podstawie porównania progu p z wartoscia wylosowana (67% szans ma chromoson o wikszym wsp przystosowania)
    IF (v_losowa <= v_prog) THEN 
        IF (v_wart_przystosowania_1 >= v_wart_przystosowania_2) THEN
            v_rodzic_id := p_pierwsze_id;
        ELSE
            v_rodzic_id := p_drugie_id;
        END IF;
    ELSE 
        IF (v_wart_przystosowania_1 >= v_wart_przystosowania_2) THEN
            v_rodzic_id := p_drugie_id;
        ELSE
            v_rodzic_id := p_pierwsze_id;
        END IF;
    END IF;
    RETURN v_rodzic_id;
  END sel_turniejowa;

--####################################################################
-- FUNKCJA GRAY ENCODER
--#################################################################### 
  FUNCTION gray_encoder(chromosom gen_tab_01.chromosom%TYPE) 
  RETURN gen_tab_01.chromosom%TYPE
  IS 
  v_chromosom gen_tab_01.chromosom%TYPE :=chromosom;
  GRAY gen_tab_01.chromosom%TYPE;
  BEGIN
    dbms_output.put_line('chromosom = ' || v_chromosom);
    GRAY := SUBSTR(v_chromosom, 1, 1);
    WHILE LENGTH(v_chromosom) > 1
    LOOP
          IF (SUBSTR(v_chromosom, 1, 1) != SUBSTR(v_chromosom, 2, 1)) THEN 
            GRAY := GRAY || '1';
          ELSE 
            GRAY := GRAY || '0'; 
          END IF;
          v_chromosom := SUBSTR(v_chromosom, 2, LENGTH(v_chromosom) - 1); 
    END LOOP;
    dbms_output.put_line('gray      = ' || GRAY); dbms_output.put_line('');
    RETURN GRAY;
  END gray_encoder;
    
--####################################################################
-- PROCEDURA ZAMIENIAJACA CHROMOSOMY W DANYM POKOLENIU Z ZAPISU BINARNEGO NA GRAY'A
--#################################################################### 
  PROCEDURE wpisz_chromoson_encoder_gray(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE)
  IS
  BEGIN
    INSERT INTO gen_tab_01_temp 
	SELECT  id 
        , id_pokolenia
        , gen_pkg.gray_encoder(chromosom)
        , chromosom_dziesietnie
        , wart_przystosowania
        , prawdopod_selekcji
    FROM gen_tab_01
    WHERE id_pokolenia = p_id_pokolenia;
                
	MERGE INTO gen_tab_01 g
	USING
	(
	  SELECT * FROM gen_tab_01_temp 
	) gt
	ON(g.id = gt.id)
	WHEN MATCHED THEN UPDATE SET
	g.chromosom = gt.chromosom;
	COMMIT;
  END wpisz_chromoson_encoder_gray;
  
--####################################################################
-- FUNKCJA GRAY DECODER
--#################################################################### 
  FUNCTION gray_decoder(GRAY gen_tab_01.chromosom%TYPE) 
  RETURN gen_tab_01.chromosom%TYPE
  IS
  v_gray gen_tab_01.chromosom%TYPE := GRAY;
  chromosom gen_tab_01.chromosom%TYPE;
  BEGIN
    chromosom := SUBSTR(v_gray, 1, 1); 
    WHILE LENGTH(v_gray) > 1
    LOOP 
      IF (SUBSTR(chromosom, LENGTH(chromosom), 1) != SUBSTR(v_gray, 2, 1)) THEN 
        chromosom := chromosom || '1'; 
      ELSE 
        chromosom := chromosom || '0'; 
      END IF;
--      v_gray := SUBSTR(v_gray, GREATEST(-LENGTH(v_gray)), -LENGTH(v_gray) - 1); 
      v_gray := SUBSTR(v_gray, 2, LENGTH(v_gray) - 1); 
    END LOOP;   
--    WHILE LENGTH(v_gray) > 1
--    LOOP 
--      IF (SUBSTR(chromosom, -1, 1) != SUBSTR(v_gray, -2, 1)) THEN 
--        chromosom := chromosom || '1'; 
--      ELSE 
--        chromosom := chromosom || '0'; 
--      END IF;
----      v_gray := SUBSTR(v_gray, GREATEST(-LENGTH(v_gray)), -LENGTH(v_gray) - 1); 
--      v_gray := SUBSTR(v_gray, 2, -LENGTH(v_gray) - 1); 
--    END LOOP;   
    RETURN chromosom ;
  END gray_decoder;
  
--####################################################################
-- PROCEDURA ZAMIENIAJACA CHROMOSOMY W DANYM POKOLENIU Z ZAPISU GRAY'A NA BINARNY
--#################################################################### 
  PROCEDURE wpisz_chromoson_decoder_gray(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE)
  IS
  BEGIN
    INSERT INTO gen_tab_01_temp 
	SELECT  id 
        , id_pokolenia
        , gen_pkg.gray_decoder(chromosom)
        , chromosom_dziesietnie
        , wart_przystosowania
        , prawdopod_selekcji
    FROM gen_tab_01
    WHERE id_pokolenia = p_id_pokolenia;
                
	MERGE INTO gen_tab_01 g
	USING
	(
	  SELECT * FROM gen_tab_01_temp 
	) gt
	ON(g.id = gt.id)
	WHEN MATCHED THEN UPDATE SET
	g.chromosom = gt.chromosom;
	COMMIT;
  END wpisz_chromoson_decoder_gray;
--#############################################################################################################################
--######################################## END MOJE FUNKCJE I PROCEDURY #####################################################
--#############################################################################################################################

  FUNCTION oblicz_prawdop_selekcji(p_id gen_tab_01.id%TYPE) 
  RETURN gen_tab_01.prawdopod_selekcji%TYPE
  IS
    v_prawdop_sel gen_tab_01.prawdopod_selekcji%TYPE;
    v_id_pokolenia gen_tab_01.id_pokolenia%TYPE;
    v_wart_przystosowania gen_tab_01.wart_przystosowania%TYPE;
    v_sum_wart_przystosowania gen_tab_01.wart_przystosowania%TYPE;
    v_prawdopod_selekcji gen_tab_01.prawdopod_selekcji%TYPE;
  BEGIN
    SELECT id_pokolenia, wart_przystosowania 
      INTO v_id_pokolenia, v_wart_przystosowania
      FROM gen_tab_01 WHERE id = p_id;
    SELECT sum(wart_przystosowania)
      INTO v_sum_wart_przystosowania
      FROM gen_tab_01 WHERE id_pokolenia = v_id_pokolenia;
    v_prawdopod_selekcji := v_wart_przystosowania / v_sum_wart_przystosowania;
    RETURN v_prawdopod_selekcji;
  END oblicz_prawdop_selekcji;
  
  PROCEDURE wpisz_prawdop_selekcji(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE)
  IS
  BEGIN
    -- kopiowanie rekordow wraz z PRAWDOPODOBIEÑSTWEM SELEKCJI
    INSERT INTO gen_tab_01_temp 
    SELECT id
         , id_pokolenia
         , chromosom
         , chromosom_dziesietnie
         , wart_przystosowania
         , gen_pkg.oblicz_prawdop_selekcji(id)
    FROM gen_tab_01
    WHERE id_pokolenia = p_id_pokolenia;
    
    -- wypisywanie do tabeli wyników PRAWDOPODOBIEÑSTWA SELEKCJI
    MERGE INTO gen_tab_01 g
    USING
    (
      SELECT * FROM gen_tab_01_temp 
    ) gt
    ON(g.id = gt.id)
    WHEN MATCHED THEN UPDATE SET
    g.prawdopod_selekcji = gt.prawdopod_selekcji;
    COMMIT;
  END wpisz_prawdop_selekcji;
  
  FUNCTION selekcja(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE, p_pierwsze_id gen_tab_01.id%TYPE := 0, p_drugi_id gen_tab_01.id%TYPE := 0, p_trzeci_id gen_tab_01.id%TYPE := 0 )  -- jesli pierwszy nie jest null to jest to selekcja drugiego do pary
  RETURN gen_tab_01.id%TYPE
  IS
    TYPE t_chromos IS RECORD (id gen_tab_01.id%TYPE,
                              prawdopod gen_tab_01.prawdopod_selekcji%TYPE,
                              prawdopod_dol gen_tab_01.prawdopod_selekcji%TYPE,
                              prawdopod_gor gen_tab_01.prawdopod_selekcji%TYPE
                              );                            
    TYPE col_t_chromos IS TABLE OF t_chromos INDEX BY BINARY_INTEGER;
    col_chromos col_t_chromos;
    v_losowa gen_tab_01.prawdopod_selekcji%TYPE;
    v_wylosowane_id gen_tab_01.id%TYPE;
  BEGIN
    SELECT id, prawdopod_selekcji, 0 , 0 
      BULK COLLECT INTO col_chromos
      FROM gen_tab_01
      WHERE id_pokolenia = p_id_pokolenia
      ORDER BY id;
      col_chromos(1).prawdopod_gor := col_chromos(1).prawdopod;  -- dla pierwszego elementu
    FOR i IN 2 .. col_chromos.count  -- dla kolejnych elementów wiec od 2
    LOOP
      col_chromos(i).prawdopod_dol := col_chromos(i-1).prawdopod_gor;
      col_chromos(i).prawdopod_gor := col_chromos(i).prawdopod_dol + col_chromos(i).prawdopod;
    END LOOP;

    LOOP  -- petla dla szukania drugiego chromosomu o id innym niz pierwszy, aby nie krzyzowac ze samym soba  
    v_losowa := dbms_random.value;
      FOR i IN 1 .. col_chromos.count  -- losowanie
      LOOP
       IF v_losowa <= col_chromos(i).prawdopod_gor THEN
           v_wylosowane_id := col_chromos(i).id;
           EXIT;
        END IF;
      END LOOP;
      EXIT WHEN v_wylosowane_id <> p_pierwsze_id AND v_wylosowane_id <> p_drugi_id AND v_wylosowane_id <> p_trzeci_id;  -- wowczas mozna zwrocic id drugiego chromosomu bo jest inne niz pierwszego
    END LOOP; 
    RETURN v_wylosowane_id;    
  END selekcja;

  FUNCTION bin2dec (binval in char) 
  RETURN number 
  IS
    i                 number;
    digits            number;
    result            number := 0;
    current_digit     char(1);
    current_digit_dec number;
  BEGIN
    digits := length(binval);
    for i in 1..digits loop
       current_digit := SUBSTR(binval, i, 1);
       current_digit_dec := to_number(current_digit);
       result := (result * 2) + current_digit_dec;
    end loop;
    return result;
  END bin2dec;

  PROCEDURE krzyzowanie_i_wpis(v_chormosom_id_1 gen_tab_01.id%TYPE, v_chormosom_id_2 gen_tab_01.id%TYPE, p_gray VARCHAR2)
  IS
    v_id_pokolenia gen_tab_01.id_pokolenia%TYPE;
    v_prawdop_krzyz NUMBER(5,2);
    v_liczba_genow NUMBER(5);
    v_pozycja_krzyzowania NUMBER(5);
    v_chromosom_1_przed_krzyz gen_tab_01.chromosom%TYPE;
    v_chromosom_2_przed_krzyz gen_tab_01.chromosom%TYPE;  
    v_chromosom_1_po_krzyz gen_tab_01.chromosom%TYPE;
    v_chromosom_2_po_krzyz gen_tab_01.chromosom%TYPE;    
  BEGIN
    -- ustalenie pokolenia do ktorego nale¿¹ chromosomy przed krzy¿owaniem
    SELECT id_pokolenia INTO v_id_pokolenia FROM gen_tab_01 WHERE id = v_chormosom_id_1;  
    -- losowanie do oceny, czy krzy¿owanie ma byæ wykonane
    v_prawdop_krzyz := trunc(DBMS_RANDOM.value,2);
  --  dbms_output.put_line('v_prawdop_krzyz '||v_prawdop_krzyz);   
    SELECT chromosom INTO v_chromosom_1_przed_krzyz FROM gen_tab_01 WHERE id = v_chormosom_id_1;
    SELECT chromosom INTO v_chromosom_2_przed_krzyz FROM gen_tab_01 WHERE id = v_chormosom_id_2;    
  --  dbms_output.put_line('chromosom 1 przed krzyzowaniem: ' ||v_chromosom_1_przed_krzyz);
  --  dbms_output.put_line('chromosom 2 przed krzyzowaniem: ' ||v_chromosom_2_przed_krzyz);
    IF v_prawdop_krzyz <= gen_pkg.v_prawdop_krzyz THEN  -- ggdy warunek krzyzowania jest spelniony
      SELECT length(chromosom) INTO v_liczba_genow FROM gen_tab_01 WHERE rownum <= 1;
      v_pozycja_krzyzowania := TRUNC(DBMS_RANDOM.value(1,v_liczba_genow+1));
  --    dbms_output.put_line('pozycja krzyzowania: ' ||v_pozycja_krzyzowania);
      v_chromosom_1_po_krzyz := substr(v_chromosom_1_przed_krzyz,1,v_pozycja_krzyzowania-1);
      v_chromosom_1_po_krzyz := v_chromosom_1_po_krzyz || substr(v_chromosom_2_przed_krzyz,v_pozycja_krzyzowania);
      v_chromosom_2_po_krzyz := substr(v_chromosom_2_przed_krzyz,1,v_pozycja_krzyzowania-1);
      v_chromosom_2_po_krzyz := v_chromosom_2_po_krzyz || substr(v_chromosom_1_przed_krzyz,v_pozycja_krzyzowania);
    ELSE  -- nie jest wykonywane krzyzowanie
      v_chromosom_1_po_krzyz := v_chromosom_1_przed_krzyz;
      v_chromosom_2_po_krzyz := v_chromosom_2_przed_krzyz;
   --   dbms_output.put_line('nie bylo krzyzowania');
    END IF;
  --  dbms_output.put_line('chromosom 1 po krzyzowaniem: ' ||v_chromosom_1_po_krzyz);
  --  dbms_output.put_line('chromosom 2 po krzyzowaniem: ' ||v_chromosom_2_po_krzyz);
--########################################################################################################################
--  jezeli parametr gray jest ustawiny na 'yes' chromosomy zostana odkodowane z zapisu gray'a do biarnego
    IF p_gray = 'a' THEN
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_1_po_krzyz,bin2dec(gen_pkg.gray_decoder(v_chromosom_1_po_krzyz)),null,null);
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_2_po_krzyz,bin2dec(gen_pkg.gray_decoder(v_chromosom_2_po_krzyz)),null,null);
        COMMIT;
--  w przeciwnym razie niezostanie wykonane dekodowania poniewa¿ chromosomy sa zakodowane binarnie
    ELSE 
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_1_po_krzyz,bin2dec(v_chromosom_1_po_krzyz),null,null);
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_2_po_krzyz,bin2dec(v_chromosom_2_po_krzyz),null,null);
        COMMIT;
    END IF;
--########################################################################################################################
  END krzyzowanie_i_wpis;

  PROCEDURE krzyzowanie_rowno_i_wpis(v_chormosom_id_1 gen_tab_01.id%TYPE, v_chormosom_id_2 gen_tab_01.id%TYPE, p_gray VARCHAR2)
  IS
    v_id_pokolenia gen_tab_01.id_pokolenia%TYPE;
    v_prawdop_krzyz NUMBER(5,2);
    v_liczba_genow NUMBER(5);
    v_chromosom_1_przed_krzyz gen_tab_01.chromosom%TYPE;
    v_chromosom_2_przed_krzyz gen_tab_01.chromosom%TYPE;  
    v_chromosom_1_po_krzyz gen_tab_01.chromosom%TYPE;
    v_chromosom_2_po_krzyz gen_tab_01.chromosom%TYPE;    
  BEGIN
    -- ustalenie pokolenia do ktorego nale¿¹ chromosomy przed krzy¿owaniem
    SELECT id_pokolenia INTO v_id_pokolenia FROM gen_tab_01 WHERE id = v_chormosom_id_1;  
    -- losowanie do oceny, czy krzy¿owanie ma byæ wykonane
    v_prawdop_krzyz := trunc(DBMS_RANDOM.value,2); 
    SELECT chromosom INTO v_chromosom_1_przed_krzyz FROM gen_tab_01 WHERE id = v_chormosom_id_1;
    SELECT chromosom INTO v_chromosom_2_przed_krzyz FROM gen_tab_01 WHERE id = v_chormosom_id_2;    
   -- dbms_output.put_line('chromosom 1 przed krzyzowaniem: ' ||v_chromosom_1_przed_krzyz);
   -- dbms_output.put_line('chromosom 2 przed krzyzowaniem: ' ||v_chromosom_2_przed_krzyz);
    IF v_prawdop_krzyz <= gen_pkg.v_prawdop_krzyz THEN  -- gdy warunek krzyzowania jest spelniony
      SELECT length(chromosom) INTO v_liczba_genow FROM gen_tab_01 WHERE rownum <= 1;
  --    dbms_output.put_line('pozycja krzyzowania: ' ||v_pozycja_krzyzowania);
  
      FOR i IN 1 .. v_liczba_genow LOOP
        IF DBMS_RANDOM.value <= 0.5 THEN
          v_chromosom_1_po_krzyz := v_chromosom_1_po_krzyz || substr(v_chromosom_2_przed_krzyz,i,1);
          v_chromosom_2_po_krzyz := v_chromosom_2_po_krzyz || substr(v_chromosom_2_przed_krzyz,i,1);          
        ELSE
          v_chromosom_1_po_krzyz := v_chromosom_1_po_krzyz || substr(v_chromosom_1_przed_krzyz,i,1);
          v_chromosom_2_po_krzyz := v_chromosom_2_po_krzyz || substr(v_chromosom_1_przed_krzyz,i,1);             
        END IF;
      END LOOP;
    ELSE  -- nie jest wykonywane krzyzowanie
      v_chromosom_1_po_krzyz := v_chromosom_1_przed_krzyz;
      v_chromosom_2_po_krzyz := v_chromosom_2_przed_krzyz;
   --   dbms_output.put_line('nie bylo krzyzowania');
    END IF;
   -- dbms_output.put_line('chromosom 1 po krzyzowaniem: ' ||v_chromosom_1_po_krzyz);
   -- dbms_output.put_line('chromosom 2 po krzyzowaniem: ' ||v_chromosom_2_po_krzyz);
--########################################################################################################################
--  jezeli parametr gray jest ustawiny na 'yes' chromosomy zostana odkodowane z zapisu gray'a do biarnego 
    IF p_gray = 'yes' THEN
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_1_po_krzyz,bin2dec(gen_pkg.gray_decoder(v_chromosom_1_po_krzyz)),null,null);
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_2_po_krzyz,bin2dec(gen_pkg.gray_decoder(v_chromosom_2_po_krzyz)),null,null);
        COMMIT;
--        w przeciwnym razie niezostanie wykonane dekodowania poniewa¿ chromosomy sa zakodowane binarnie
    ELSE
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_1_po_krzyz,bin2dec(v_chromosom_1_po_krzyz),null,null);
        INSERT INTO gen_tab_01 (id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji)
          VALUES (v_id_pokolenia+1,v_chromosom_2_po_krzyz,bin2dec(v_chromosom_2_po_krzyz),null,null);
        COMMIT;
    END IF;
--########################################################################################################################    
  END krzyzowanie_rowno_i_wpis;

  PROCEDURE mutacja_i_wpis(p_id_pokolenia gen_tab_01.id_pokolenia%TYPE, p_gray VARCHAR2)  
  IS
    v_liczba_genow NUMBER(5);  
    v_pozycja_mutacji NUMBER(5);  
    v_prawdop_mutacji NUMBER(5,4);
    v_chromosom_przed_mutacja gen_tab_01.chromosom%TYPE;
    v_chromosom_po_mutacja gen_tab_01.chromosom%TYPE; 
    v_gen_mutujacy VARCHAR2(1);
  BEGIN
    SELECT length(chromosom) INTO v_liczba_genow FROM gen_tab_01 WHERE rownum <= 1;  
    FOR i IN (SELECT id FROM gen_tab_01 WHERE id_pokolenia = p_id_pokolenia ORDER BY id)
    LOOP
      v_prawdop_mutacji := trunc(DBMS_RANDOM.value,4);  
      IF v_prawdop_mutacji <= gen_pkg.v_prawdop_mutacji THEN  -- gdy warunek mutacji jest spelniony 
        dbms_output.put_line('wyst¹pia mutacja genu');    
        v_pozycja_mutacji := TRUNC(DBMS_RANDOM.value(1,v_liczba_genow+1));    
        SELECT chromosom INTO v_chromosom_przed_mutacja FROM gen_tab_01 WHERE id = i.id;   
        v_chromosom_po_mutacja := substr(v_chromosom_przed_mutacja,1,v_pozycja_mutacji-1);
        v_gen_mutujacy := substr(v_chromosom_przed_mutacja,v_pozycja_mutacji,1);
        -- mutacja genu
        IF v_gen_mutujacy = 0 THEN v_gen_mutujacy := 1;
          ELSE v_gen_mutujacy := 0;
        END IF;
        v_chromosom_po_mutacja := v_chromosom_po_mutacja || v_gen_mutujacy;
        v_chromosom_po_mutacja := v_chromosom_po_mutacja || substr(v_chromosom_przed_mutacja,v_pozycja_mutacji+1);
        -- uaktualnienie chromosomu
--########################################################################################################################
--  jezeli parametr gray jest ustawiny na 'yes' chromosomy zostana odkodowane z zapisu gray'a do biarnego 
        IF p_gray = 'yes' THEN
            UPDATE gen_tab_01 SET chromosom = v_chromosom_po_mutacja
                                , chromosom_dziesietnie = bin2dec(gen_pkg.gray_decoder(v_chromosom_po_mutacja))
                              WHERE id = i.id;
            COMMIT;
        ELSE
            UPDATE gen_tab_01 SET chromosom = v_chromosom_po_mutacja
                                    , chromosom_dziesietnie = bin2dec(v_chromosom_po_mutacja)
                                  WHERE id = i.id;
                COMMIT;
        END IF;
--########################################################################################################################  
      END IF;
    END LOOP;
  END mutacja_i_wpis;

  PROCEDURE algorytm(p_liczba_pokolen NUMBER
                   , p_prawdop_krzyz NUMBER
                   , p_prawdop_mutacji NUMBER
                   , p_sred_przystosowanie_docelowe gen_tab_01.wart_przystosowania%TYPE
                   , p_metoda_selekcji VARCHAR2
                   , p_metoda_krzyzowania VARCHAR2
                   , p_gray VARCHAR2)
  IS
    v_chormosom_id_1 gen_tab_01.id%TYPE;
    v_chormosom_id_2 gen_tab_01.id%TYPE;
    v_chormosom_id_3 gen_tab_01.id%TYPE;
    v_chormosom_id_4 gen_tab_01.id%TYPE;
    v_liczba_chromos NUMBER(5);
    v_id_pokolenia NUMBER(5) := 1;
    v_sred_przystosowanie_docelowe gen_tab_01.wart_przystosowania%TYPE;
  BEGIN
    -- 0.
    -- ustawienie zmiennych
    gen_pkg.v_prawdop_krzyz := p_prawdop_krzyz;    
    gen_pkg.v_prawdop_mutacji := p_prawdop_mutacji;
    -- Resetowanie danych - od nowa wstawione chromosomy oraz licznik sekwencji od 1
    resetuj_dane;
--########################################################################################################################
--  jezeli parametr gray jest ustawiny na 'yes' chromosomy zostana zakodowane z zapisu biarnego na kod gray'a
    iF p_gray = 'yes' THEN
      wpisz_chromoson_encoder_gray(v_id_pokolenia);
    END IF;
--########################################################################################################################
    LOOP
      -- 1.
      -- wypenienie tabeli wejsciowej wartoœciami funkcji przystosowania / dopasowania
      -- procedura wewnêtrznie wywoluje funkcjê: oblicz_wart_przystosowania 
      wpisz_wart_przystosowania;
      -- 1b. 
      -- przeskalowanie wartosci funkcji dopasowania
      -- domyœlnie jest metoda PROPORCJONALNA (zale¿y od wart. funkcji dopasowania i kola ruletki) 
      IF p_metoda_selekcji='skal' THEN
        przeskal_i_wpisz_wart_przystos(v_id_pokolenia);   
      END IF;
      -- 2.
      -- wypenienie tabeli wejsciowej wartoœciami pradopodobieñstwa selekcji
      -- procedura wewnêtrznie wywoluje funkcjê: oblicz_prawdop_selekcji
      wpisz_prawdop_selekcji(v_id_pokolenia);
--########################################################################################################################
--    jezeli paramtr selekcji jest ustawiony na rank to wywkona siê selekcja rankingowa
      IF p_metoda_selekcji='rank' THEN
        sel_rankingowa(v_id_pokolenia);   
      END IF;
--######################################################################################################################## 
      -- 3. wyliczenie œredniej wartoœci funkcji przystosowania
      SELECT avg(wart_przystosowania) INTO v_sred_przystosowanie_docelowe FROM gen_tab_01 WHERE id_pokolenia = v_id_pokolenia;   
      dbms_output.put_line('Srednie przystosowanie populacji ' || v_id_pokolenia || ' = ' || v_sred_przystosowanie_docelowe);       
      -- 4. weryfikacja, czy przerwac pêtle
      v_id_pokolenia := v_id_pokolenia + 1;
      EXIT WHEN v_id_pokolenia > p_liczba_pokolen
             OR v_sred_przystosowanie_docelowe >= p_sred_przystosowanie_docelowe;      
      -- 5.
      -- selekcja dwóch chromosomów do krzy¿owania i mutacji
      -- przekazujemy jako parametr nr pokolenia (ale obecnego, a nie powiêkszonego o 1)
      -- i id pierwszego chromosomu przy drugim losowaniu, aby unikn¹æ przy¿owania chromosomu z samym sob¹
--###########################################################################################################################
--   jezeli paramtr selekcji jest ustawiony na turn to wywkona siê selekcja turniejowa
     IF (p_metoda_selekcji='turn') THEN
        -- losowanie dwóch par potencjalnych rodziców     
        v_chormosom_id_1 := selekcja(v_id_pokolenia-1);
        v_chormosom_id_2 := selekcja(v_id_pokolenia-1, v_chormosom_id_1);
        v_chormosom_id_3 := selekcja(v_id_pokolenia-1, v_chormosom_id_1, v_chormosom_id_2);
        v_chormosom_id_4 := selekcja(v_id_pokolenia-1, v_chormosom_id_1, v_chormosom_id_2, v_chormosom_id_3);
        -- selekcja truniejowa okreslajaca rodziców nowego pokolenia
        v_chormosom_id_1 := sel_turniejowa(v_chormosom_id_1, v_chormosom_id_2); 
        v_chormosom_id_2 := sel_turniejowa(v_chormosom_id_3, v_chormosom_id_4); 
--   w przeciwnym razie rodzice kolejnego pokolenia zostana wybrani w tradycyjny sposób
     ELSE
        v_chormosom_id_1 := selekcja(v_id_pokolenia-1);
        v_chormosom_id_2 := selekcja(v_id_pokolenia-1, v_chormosom_id_1);
     END IF;
--##########################################################################################################################
--      dbms_output.put_line(v_chormosom_id_1 || ' - ' || v_chormosom_id_2); 
      -- 6. krzyzowanie
      -- nale¿y wygenerowaæ cal¹ now¹ populacjê w danym pokoleniu - st¹d pêtla
      SELECT count(*) INTO v_liczba_chromos FROM gen_tab_01 WHERE id_pokolenia = 1;
      FOR i IN 1 .. v_liczba_chromos / 2 LOOP
        -- przekazujemy id dwoch chromosomow      
      IF p_metoda_krzyzowania='jedno_punkt' THEN
        krzyzowanie_i_wpis(v_chormosom_id_1,v_chormosom_id_2, p_gray);
      ELSIF p_metoda_krzyzowania='rownomierne' THEN
        krzyzowanie_rowno_i_wpis(v_chormosom_id_1,v_chormosom_id_2, p_gray);
      END IF;
      END LOOP;
      -- 7. Mutowanie kolejnych chromosomow
      mutacja_i_wpis(v_id_pokolenia, p_gray);
    END LOOP;
  END algorytm;
  
END gen_pkg;
/
show err


-- uruchamienie 
-- dwa parametry to warunki przerwania pêtli tworz¹cej kolejne pokolenia:
-- 1 - liczba pokolen, 
-- 2 - prawdopodobieñstwo krzy¿owania
-- 3 - prawdopodobieñstwo mutacji
-- 4 - srednie dopasowanie docelowo, 
-- 5 - metoda selekcji: 'prop' - proporcjonalna, oparta o prawdopodobienstwo i kolo ruletki
--                      'skal' - ze skalowaniem wartoœci funkcji dopasowania
-- 6 - metoda krzy¿owania: 'jedno_punkt' - jednopunktowe
--                         'rownomierne' - równomierne / jednolite / jednostajne / uniform

--ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,';
--exec gen_pkg.algorytm(25, 0.75, 0.05, 0.5, 'prop', 'jedno_punkt', 'no');
--exec gen_pkg.algorytm(25, 0.75, 0.05, 3, 'skal', 'jedno_punkt', 'no');
--exec gen_pkg.algorytm(25, 0.75, 0.05, 0.5, 'prop', 'rownomierne', 'no');
--exec gen_pkg.algorytm(25, 0.75, 0.05, 3, 'skal', 'rownomierne', 'no');

-- rankingowa: rank   turniejowa: turn   gray: 'yes'
-- weryfikacja wyników 
--ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,';
--exec gen_pkg.algorytm(25, 0.75, 0.05, 6, 'turn', 'jedno_punkt', 'yes');
--SELECT id, id_pokolenia, chromosom, chromosom_dziesietnie, wart_przystosowania, prawdopod_selekcji
--FROM gen_tab_01 ORDER BY id_pokolenia, id;
