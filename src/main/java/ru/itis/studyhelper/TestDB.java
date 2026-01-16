package ru.itis.studyhelper;

import ru.itis.studyhelper.dao.UserDao;
import ru.itis.studyhelper.service.UserService;
import ru.itis.studyhelper.utils.DatabaseUtil;

import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) throws SQLException {

        try {
            UserService service = new UserService(new UserDao());

            service.register("bazarnoffroman@yandex.ru",
                    "qwerty",
                    "Bazarnoff Roman Aleksandrovich",
                    "KFU",
                    "ITIS",
                    2);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close();
        }


    }
}
