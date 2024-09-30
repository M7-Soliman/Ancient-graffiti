--
-- PostgreSQL database dump
--

-- Dumped from database version 14.7 (Ubuntu 14.7-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.7 (Ubuntu 14.7-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: city_key_sequence; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.city_key_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.city_key_sequence OWNER TO sprenkle;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cities; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.cities (
    id integer DEFAULT nextval('public.city_key_sequence'::regclass) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(300),
    pleiades_id character varying(15) NOT NULL
);


ALTER TABLE public.cities OWNER TO sprenkle;

--
-- Name: column_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.column_id_seq OWNER TO sprenkle;

--
-- Name: columns; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.columns (
    id integer DEFAULT nextval('public.column_id_seq'::regclass) NOT NULL,
    roman_numeral text NOT NULL,
    decimal_number integer
);


ALTER TABLE public.columns OWNER TO sprenkle;

--
-- Name: drawing_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.drawing_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.drawing_tag_id_seq OWNER TO sprenkle;

--
-- Name: drawing_tags; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.drawing_tags (
    id integer DEFAULT nextval('public.drawing_tag_id_seq'::regclass) NOT NULL,
    name character varying(30),
    description character varying(100)
);


ALTER TABLE public.drawing_tags OWNER TO sprenkle;

--
-- Name: eagle_inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.eagle_inscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.eagle_inscription_id_seq OWNER TO sprenkle;

--
-- Name: epidoc_contributions; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.epidoc_contributions (
    inscription_id character(9),
    user_name character varying(30),
    comment text,
    date_modified character varying(8)
);


ALTER TABLE public.epidoc_contributions OWNER TO sprenkle;

--
-- Name: existing_edr_sequence; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.existing_edr_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.existing_edr_sequence OWNER TO sprenkle;

--
-- Name: existing_edr_ids; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.existing_edr_ids (
    id integer DEFAULT nextval('public.existing_edr_sequence'::regclass) NOT NULL,
    edr_id character(9)
);


ALTER TABLE public.existing_edr_ids OWNER TO sprenkle;

--
-- Name: featured_graffiti_info; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.featured_graffiti_info (
    graffiti_id character(9) NOT NULL,
    commentary text,
    preferred_image character varying(30)
);


ALTER TABLE public.featured_graffiti_info OWNER TO sprenkle;

--
-- Name: figural_graffiti_info; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.figural_graffiti_info (
    graffiti_id character(9) NOT NULL,
    description_in_latin text,
    description_in_english text
);


ALTER TABLE public.figural_graffiti_info OWNER TO sprenkle;

CREATE TABLE public.poetic_graffiti_info (
	graffiti_id character(9) NOT NULL,
	meter character varying(100),
	author character varying(300),
	confirmed boolean,
	poetry_type character varying(100)

);

ALTER TABLE public.poetic_graffiti_info OWNER TO sprenkle;

--
-- Name: graffititothemes_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.graffititothemes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.graffititothemes_id_seq OWNER TO sprenkle;

--
-- Name: graffititothemes; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.graffititothemes (
    id integer DEFAULT nextval('public.graffititothemes_id_seq'::regclass) NOT NULL,
    graffito_id character(9) NOT NULL,
    theme_id integer NOT NULL
);


ALTER TABLE public.graffititothemes OWNER TO sprenkle;

--
-- Name: graffitotodrawingtags_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.graffitotodrawingtags_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.graffitotodrawingtags_id_seq OWNER TO sprenkle;

--
-- Name: graffitotodrawingtags; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.graffitotodrawingtags (
    id integer DEFAULT nextval('public.graffitotodrawingtags_id_seq'::regclass) NOT NULL,
    graffito_id character(9) NOT NULL,
    drawing_tag_id integer NOT NULL
);


ALTER TABLE public.graffitotodrawingtags OWNER TO sprenkle;

--
-- Name: index_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.index_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.index_id_seq OWNER TO sprenkle;

--
-- Name: index; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.index (
    id integer DEFAULT nextval('public.index_id_seq'::regclass) NOT NULL,
    term_id integer NOT NULL,
    graffiti_id character(9) NOT NULL,
    hit text NOT NULL,
    content text NOT NULL
);


ALTER TABLE public.index OWNER TO sprenkle;

--
-- Name: inscription_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.inscription_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.inscription_id_seq OWNER TO sprenkle;

--
-- Name: inscriptions; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.inscriptions (
    id integer DEFAULT nextval('public.inscription_id_seq'::regclass) NOT NULL,
    graffiti_id character(9) NOT NULL,
    commentary text,
    content_translation text,
    lang_in_english character varying(30),
    writing_style_in_english character varying(30),
    height_from_ground character varying(30),
    graffito_height character varying(30),
    graffito_length character varying(30),
    letter_height_min character varying(20),
    letter_height_max character varying(20),
    property_id integer,
    cil character varying(100),
    langner character varying(100),
    has_figural_component boolean DEFAULT false,
    individual_letter_heights text,
    caption character varying(200),
    is_featured_translation boolean DEFAULT false,
    is_featured_figural boolean DEFAULT false,
    content_epidocified text,
    letter_with_flourishes_height_min character varying(10),
    letter_with_flourishes_height_max character varying(10),
    is_themed boolean DEFAULT false,
    on_facade boolean,
    segment_id integer,
    contributors text,
    update_of_cil boolean DEFAULT false,
    ancient_city character varying(30),
    find_spot character varying(200),
    measurements character varying(100),
    writing_style character varying(30),
    language character varying(30),
    content text,
    bibliography text,
    apparatus text,
    apparatus_displayed text,
    date_beginning character varying(100),
    date_end character varying(100),
    date_explanation character varying(40),
    last_revision text,
    editor text,
    principle_contributors text,
    support_desc text,
    layout_desc text,
    handnote_desc text,
    precise_location text,
    on_column boolean DEFAULT false,
    column_id integer,
    is_poetic boolean
);


ALTER TABLE public.inscriptions OWNER TO sprenkle;

--
-- Name: insula_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.insula_id_seq
    START WITH 10000
    INCREMENT BY 1
    MINVALUE 10000
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.insula_id_seq OWNER TO sprenkle;

--
-- Name: insula; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.insula (
    id integer DEFAULT nextval('public.insula_id_seq'::regclass) NOT NULL,
    modern_city character varying(30),
    short_name character varying(20),
    full_name character varying(100),
    pleiades_id character varying(15)
);


ALTER TABLE public.insula OWNER TO sprenkle;

--
-- Name: lemma; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.lemma (
    term text NOT NULL,
    lemma text NOT NULL
);


ALTER TABLE public.lemma OWNER TO sprenkle;

--
-- Name: more_featured_graffiti_info; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.more_featured_graffiti_info (
    graffiti_id character(9) NOT NULL,
    content text,
    translation text,
    cil text,
    image text,
    commentary text
);


ALTER TABLE public.more_featured_graffiti_info OWNER TO sprenkle;

--
-- Name: more_graffititothemes_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.more_graffititothemes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.more_graffititothemes_id_seq OWNER TO sprenkle;

--
-- Name: more_graffititothemes; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.more_graffititothemes (
    id integer DEFAULT nextval('public.more_graffititothemes_id_seq'::regclass) NOT NULL,
    graffito_id character(9) NOT NULL,
    theme_id integer NOT NULL
);


ALTER TABLE public.more_graffititothemes OWNER TO sprenkle;

--
-- Name: names; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.names (
    name text,
    name_type text,
    person_type text,
    gender text
);


ALTER TABLE public.names OWNER TO sprenkle;

--
-- Name: photos; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.photos (
    graffiti_id character(9),
    photo_id character varying(20)
);


ALTER TABLE public.photos OWNER TO sprenkle;

--
-- Name: places; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.places (
    name text
);


ALTER TABLE public.places OWNER TO sprenkle;

--
-- Name: property_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.property_id_seq OWNER TO sprenkle;

--
-- Name: properties; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.properties (
    id integer DEFAULT nextval('public.property_id_seq'::regclass) NOT NULL,
    property_number character varying(20),
    additional_properties character varying(30),
    property_name character varying(70),
    italian_property_name character varying(70),
    insula_id integer,
    pleiades_id character varying(15),
    commentary text,
    is_insula_based boolean,
    osm_id character varying(15),
    osm_way_id character varying(15),
    english_property_name character varying(70)
);


ALTER TABLE public.properties OWNER TO sprenkle;

--
-- Name: property_links; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.property_links (
    property_id integer,
    link_name character varying(70),
    link character varying(200)
);


ALTER TABLE public.property_links OWNER TO sprenkle;

--
-- Name: property_type_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.property_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.property_type_id_seq OWNER TO sprenkle;

--
-- Name: propertytopropertytype; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.propertytopropertytype (
    property_id integer NOT NULL,
    property_type integer NOT NULL
);


ALTER TABLE public.propertytopropertytype OWNER TO sprenkle;

--
-- Name: propertytypes; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.propertytypes (
    id integer DEFAULT nextval('public.property_type_id_seq'::regclass) NOT NULL,
    name character varying(30),
    commentary character varying(200),
    parent_id integer,
    is_parent boolean
);


ALTER TABLE public.propertytypes OWNER TO sprenkle;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.roles (
    id integer,
    role character varying(30)
);


ALTER TABLE public.roles OWNER TO sprenkle;

--
-- Name: segment_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.segment_id_seq
    START WITH 30000
    INCREMENT BY 1
    MINVALUE 30000
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.segment_id_seq OWNER TO sprenkle;

--
-- Name: segments; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.segments (
    id integer DEFAULT nextval('public.segment_id_seq'::regclass) NOT NULL,
    segment_name character varying(50),
    street_id integer,
    display_name text,
    hidden boolean DEFAULT false
);


ALTER TABLE public.segments OWNER TO sprenkle;

--
-- Name: street_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.street_id_seq
    START WITH 20000
    INCREMENT BY 1
    MINVALUE 20000
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.street_id_seq OWNER TO sprenkle;

--
-- Name: streets; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.streets (
    id integer DEFAULT nextval('public.street_id_seq'::regclass) NOT NULL,
    street_name character varying(100),
    city text
);


ALTER TABLE public.streets OWNER TO sprenkle;

--
-- Name: terms_id_seq; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.terms_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.terms_id_seq OWNER TO sprenkle;

--
-- Name: terms; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.terms (
    term_id integer DEFAULT nextval('public.terms_id_seq'::regclass) NOT NULL,
    term character varying(25) NOT NULL,
    category character varying(25),
    display boolean,
    part_of_speech character varying(25),
    language character varying(25),
    sort_key text
);


ALTER TABLE public.terms OWNER TO sprenkle;

--
-- Name: theme_ids_sequence; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.theme_ids_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.theme_ids_sequence OWNER TO sprenkle;

--
-- Name: themes; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.themes (
    theme_id integer DEFAULT nextval('public.theme_ids_sequence'::regclass) NOT NULL,
    name character varying(80),
    description text
);


ALTER TABLE public.themes OWNER TO sprenkle;

--
-- Name: user_ids_sequence; Type: SEQUENCE; Schema: public; Owner: sprenkle
--

CREATE SEQUENCE public.user_ids_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_ids_sequence OWNER TO sprenkle;

--
-- Name: users; Type: TABLE; Schema: public; Owner: sprenkle
--

CREATE TABLE public.users (
    id integer DEFAULT nextval('public.user_ids_sequence'::regclass) NOT NULL,
    password character varying(64) NOT NULL,
    username character varying(20) NOT NULL,
    name character varying(30),
    role character varying(30),
    enabled boolean,
    hash character varying
);


ALTER TABLE public.users OWNER TO sprenkle;

--
-- Name: inscriptions agp_inscription_annotations_eagle_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.inscriptions
    ADD CONSTRAINT agp_inscription_annotations_eagle_id_key UNIQUE (graffiti_id);


--
-- Name: inscriptions agp_inscription_annotations_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.inscriptions
    ADD CONSTRAINT agp_inscription_annotations_pkey PRIMARY KEY (id);


--
-- Name: cities cities_unique_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT cities_unique_key UNIQUE (name);


--
-- Name: cities city_primary_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT city_primary_key PRIMARY KEY (id);


--
-- Name: drawing_tags drawing_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.drawing_tags
    ADD CONSTRAINT drawing_tags_pkey PRIMARY KEY (id);


--
-- Name: inscriptions eagle_inscriptions_eagle_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.inscriptions
    ADD CONSTRAINT eagle_inscriptions_eagle_id_key UNIQUE (graffiti_id);


--
-- Name: featured_graffiti_info featured_graffiti_primary_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.featured_graffiti_info
    ADD CONSTRAINT featured_graffiti_primary_key PRIMARY KEY (graffiti_id);


--
-- Name: figural_graffiti_info figural_graffiti_info_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.figural_graffiti_info
    ADD CONSTRAINT figural_graffiti_info_pkey PRIMARY KEY (graffiti_id);

ALTER TABLE ONLY public.poetic_graffiti_info
    ADD CONSTRAINT poetic_graffiti_info_pkey PRIMARY KEY (graffiti_id);

--
-- Name: graffititothemes graffititothemes_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffititothemes
    ADD CONSTRAINT graffititothemes_pkey PRIMARY KEY (id);


--
-- Name: graffitotodrawingtags graffitotodrawingtags_graffito_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_graffito_id_key UNIQUE (graffito_id, drawing_tag_id);


--
-- Name: graffitotodrawingtags graffitotodrawingtags_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_pkey PRIMARY KEY (id);


--
-- Name: inscriptions inscriptions_graffiti_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.inscriptions
    ADD CONSTRAINT inscriptions_graffiti_id_key UNIQUE (graffiti_id);


--
-- Name: insula insula_modern_city_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.insula
    ADD CONSTRAINT insula_modern_city_key UNIQUE (modern_city, short_name);


--
-- Name: insula insula_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.insula
    ADD CONSTRAINT insula_pkey PRIMARY KEY (id);


--
-- Name: lemma lemma_unique_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.lemma
    ADD CONSTRAINT lemma_unique_key UNIQUE (term);


--
-- Name: more_graffititothemes more_graffititothemes_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.more_graffititothemes
    ADD CONSTRAINT more_graffititothemes_id_key UNIQUE (graffito_id, theme_id);


--
-- Name: names names_name_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.names
    ADD CONSTRAINT names_name_key UNIQUE (name);


--
-- Name: photos photos_graffiti_id_photo_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.photos
    ADD CONSTRAINT photos_graffiti_id_photo_id_key UNIQUE (graffiti_id, photo_id);


--
-- Name: places places_name_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.places
    ADD CONSTRAINT places_name_key UNIQUE (name);


--
-- Name: properties properties_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (id);


--
-- Name: properties properties_uniq_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT properties_uniq_key UNIQUE (insula_id, property_number);


--
-- Name: propertytopropertytype propertyToPropertyTypes_primary_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.propertytopropertytype
    ADD CONSTRAINT "propertyToPropertyTypes_primary_key" PRIMARY KEY (property_id, property_type);


--
-- Name: propertytypes propertytypes_name_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.propertytypes
    ADD CONSTRAINT propertytypes_name_key UNIQUE (name);


--
-- Name: propertytypes propertytypes_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.propertytypes
    ADD CONSTRAINT propertytypes_pkey PRIMARY KEY (id);


--
-- Name: segments segments_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.segments
    ADD CONSTRAINT segments_pkey PRIMARY KEY (id);


--
-- Name: streets streets_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.streets
    ADD CONSTRAINT streets_pkey PRIMARY KEY (id);


--
-- Name: terms terms_id_key; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.terms
    ADD CONSTRAINT terms_id_key UNIQUE (term_id);


--
-- Name: themes themes_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.themes
    ADD CONSTRAINT themes_pkey PRIMARY KEY (theme_id);


--
-- Name: graffititothemes themes_unique; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffititothemes
    ADD CONSTRAINT themes_unique UNIQUE (graffito_id, theme_id);


--
-- Name: drawing_tags unique_drawing_tag_name; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.drawing_tags
    ADD CONSTRAINT unique_drawing_tag_name UNIQUE (name);


--
-- Name: themes unique_name; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.themes
    ADD CONSTRAINT unique_name UNIQUE (name);


--
-- Name: users username_unique; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT username_unique UNIQUE (username);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: inscriptions agp_inscription_annotations_properties_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.inscriptions
    ADD CONSTRAINT agp_inscription_annotations_properties_fkey FOREIGN KEY (property_id) REFERENCES public.properties(id) ON DELETE CASCADE;


--
-- Name: featured_graffiti_info featured_graffiti_edr_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.featured_graffiti_info
    ADD CONSTRAINT featured_graffiti_edr_fkey FOREIGN KEY (graffiti_id) REFERENCES public.inscriptions(graffiti_id) ON DELETE CASCADE;


--
-- Name: figural_graffiti_info figural_graffiti_info_eagle_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.figural_graffiti_info
    ADD CONSTRAINT figural_graffiti_info_eagle_id_fkey FOREIGN KEY (graffiti_id) REFERENCES public.inscriptions(graffiti_id) ON DELETE CASCADE;


    
ALTER TABLE ONLY public.poetic_graffiti_info
    ADD CONSTRAINT poetic_graffiti_info_eagle_id_fkey FOREIGN KEY (graffiti_id) REFERENCES public.inscriptions(graffiti_id) ON DELETE CASCADE;  
  
--
-- Name: graffititothemes graffititothemes_graffito_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffititothemes
    ADD CONSTRAINT graffititothemes_graffito_id_fkey FOREIGN KEY (graffito_id) REFERENCES public.inscriptions(graffiti_id) ON DELETE CASCADE;


--
-- Name: graffititothemes graffititothemes_theme_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffititothemes
    ADD CONSTRAINT graffititothemes_theme_id_fkey FOREIGN KEY (theme_id) REFERENCES public.themes(theme_id);


--
-- Name: graffitotodrawingtags graffitotodrawingtags_drawing_tag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_drawing_tag_id_fkey FOREIGN KEY (drawing_tag_id) REFERENCES public.drawing_tags(id);


--
-- Name: graffitotodrawingtags graffitotodrawingtags_graffito_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.graffitotodrawingtags
    ADD CONSTRAINT graffitotodrawingtags_graffito_id_fkey FOREIGN KEY (graffito_id) REFERENCES public.inscriptions(graffiti_id) ON DELETE CASCADE;


--
-- Name: index index_graffiti_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.index
    ADD CONSTRAINT index_graffiti_id_fkey FOREIGN KEY (graffiti_id) REFERENCES public.inscriptions(graffiti_id) ON DELETE CASCADE;


--
-- Name: index index_terms_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.index
    ADD CONSTRAINT index_terms_id_fkey FOREIGN KEY (term_id) REFERENCES public.terms(term_id) ON DELETE CASCADE;


--
-- Name: inscriptions inscription_segment_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.inscriptions
    ADD CONSTRAINT inscription_segment_fkey FOREIGN KEY (segment_id) REFERENCES public.segments(id) ON DELETE CASCADE;


--
-- Name: photos photos_graffiti_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.photos
    ADD CONSTRAINT photos_graffiti_id_fkey FOREIGN KEY (graffiti_id) REFERENCES public.inscriptions(graffiti_id) ON DELETE CASCADE;


--
-- Name: properties properties_insula_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT properties_insula_id_fkey FOREIGN KEY (insula_id) REFERENCES public.insula(id) ON DELETE CASCADE;


--
-- Name: property_links property_links_property_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.property_links
    ADD CONSTRAINT property_links_property_id_fkey FOREIGN KEY (property_id) REFERENCES public.properties(id) ON DELETE CASCADE;


--
-- Name: propertytopropertytype propertytopropertytype_property_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.propertytopropertytype
    ADD CONSTRAINT propertytopropertytype_property_id_fkey FOREIGN KEY (property_id) REFERENCES public.properties(id) ON DELETE CASCADE;


--
-- Name: propertytopropertytype propertytopropertytype_property_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sprenkle
--

ALTER TABLE ONLY public.propertytopropertytype
    ADD CONSTRAINT propertytopropertytype_property_type_fkey FOREIGN KEY (property_type) REFERENCES public.propertytypes(id);


--
-- Name: TABLE cities; Type: ACL; Schema: public; Owner: sprenkle
--

GRANT SELECT,REFERENCES,UPDATE ON TABLE public.cities TO PUBLIC;


--
-- Name: TABLE featured_graffiti_info; Type: ACL; Schema: public; Owner: sprenkle
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.featured_graffiti_info TO PUBLIC;


--
-- PostgreSQL database dump complete
--

