package org.testHibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testHibernate.models.Director;
import org.testHibernate.models.Film;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App

    //---- Каскадирование в Hibernate не настроено, поэтому для правильной работы (в том числе хеширования hibernate)
    //---- будем устанавливать связи с оббеих сторон
    //---- на стороное БД каскадирование - on delete set null
{
    public static void main( String[] args ){
        Configuration configuration = new Configuration().addAnnotatedClass(Director.class)
                .addAnnotatedClass(Film.class);
//**********************************************************************************************************
        //Получаем список всех фильмов выбранного режисёра
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession())
        //tr-with-resource автоматически закроет сессию (finaly{session.close()})- не нужен
        {
            session.beginTransaction();

            Director director = session.get(Director.class,1);
            List<Film> filmList = director.getFilms();
            for (Film f: filmList){
                System.out.println(f);
            }

            session.getTransaction().commit();
        }

//***************************************************************************************************************
        //Получаем какой-то фильм(любой) далее получаем режисёра, который его снял
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();

            Film film = session.get(Film.class,5);
            Director director = film.getDirector();
            System.out.println(director);
            session.getTransaction().commit();
        }
//*************************************************************************************************************
        //-----Добавляем новый фильм для режисёра "id = 1"
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();

            Director director = session.get(Director.class,1);
//            //----Устанавливаем связь в hibernate на стороне film путём установки в конструктор экз-р director
            Film film = new Film(director,"Super sus");
//            //---Сохраняем фильм  бд, иначе в output-е будет виден SQL, но у film объекта будет id = 0
//            // ---- и об-та не будет в базе
            session.save(film);
//            //----устанавливаем связь в hibernate на стороне director
            director.getFilms().add(film);
            System.out.println(director.getFilms());
            session.getTransaction().commit();
        }
//*************************************************************************************************************
        //---Создаём новго режисера и новый фильм и свзываем их между собой - т е уст-ем связи в бд по внеш ключу
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();

           Director director = new Director("Irinka");
           Film film = new Film(director,"Poo");
           //----Необходимо создать новый список тк ксли вызвать метод .getFilms() - выдаст NullPointerException
            //---из-за того что мы пытаемся получить несуществующий список
            //---Collections.singletonList(film) - позваляет нам создать список с одним элементом,
            // ---данный метод создаёт неизменяемую коллекцию, чтобы мы могли с ней далее взаи-ть мы помещаем
            //---этот метод в конструктор ArrayList<>(*)
           director.setFilms( new ArrayList<>(Collections.singletonList(film)));
           session.save(director);
           session.save(film);
           film.setDirector(director);
            System.out.println(film.getDirector());

            session.getTransaction().commit();
        }
//************************************************************************************************************
        //Меняем режисёра у существующего фильма
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();

            Film film = session.get(Film.class,4);
            Director director = session.get(Director.class,1);
            //Получаем режисёра(бывшего).получаем список его фильмов.удаляем выбранный фильм из списка
            film.getDirector().getFilms().remove(film);
            //получаем список новго режисёра. добавляем наш фильм в список
            director.getFilms().add(film);
            //на стороне film  устанавливаем режисёра
            film.setDirector(director);

            session.getTransaction().commit();
        }
//*************************************************************************************************************
        //**********************************************************************************************************
        //Получаем список всех фильмов выбранного режисёра
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession())
        //tr-with-resource автоматически закроет сессию (finaly{session.close()})- не нужен
        {
            session.beginTransaction();

            Director director = session.get(Director.class,1);
            List<Film> filmList = director.getFilms();
            for (Film f: filmList){
                System.out.println(f);
            }

            session.getTransaction().commit();
        }

//***************************************************************************************************************
        //Получаем какой-то фильм(любой) далее получаем режисёра, который его снял
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();

            Film film = session.get(Film.class,5);
            Director director = film.getDirector();
            System.out.println(director);
            session.getTransaction().commit();
        }
//*************************************************************************************************************
        //-----Добавляем новый фильм для режисёра "id = 1"
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();

            Director director = session.get(Director.class,1);
//            //----Устанавливаем связь в hibernate на стороне film путём установки в конструктор экз-р director
            Film film = new Film(director,"Super sus");
//            //---Сохраняем фильм  бд, иначе в output-е будет виден SQL, но у film объекта будет id = 0
//            // ---- и об-та не будет в базе
            session.save(film);
//            //----устанавливаем связь в hibernate на стороне director
            director.getFilms().add(film);
            System.out.println(director.getFilms());
            session.getTransaction().commit();
        }
//*************************************************************************************************************
        //---Удаляем фильм у любого режисёра
        try(SessionFactory sessionFactory = configuration.buildSessionFactory();
            Session session = sessionFactory.getCurrentSession()){
            Director director = session.get(Director.class, 3);
            Film film = session.get(Film.class,5);
            //Удаляем фильм из списка по индексу(у данного режисёра есть фильм под индексом 5)
            //Это SQL запрос
            director.getFilms().remove(5);
            //На стороне фильма устанавливаем режисёра в null
            //Это для правильного состояния Hibernate кеша - не SQL
            film.setDirector(null);

            //Далее проверка - для себя
            List<Film> films = director.getFilms();
            for(Film s: films){
                System.out.println(s);
            }
            session.getTransaction().commit();
        }
    }
}
