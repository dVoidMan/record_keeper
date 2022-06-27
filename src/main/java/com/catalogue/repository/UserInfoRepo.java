package com.catalogue.repository;

import com.catalogue.model.UserInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserInfoRepo extends JpaRepository<UserInfoModel, String> {

    // returns true if email and password of a user is found in the DB
    @Query("SELECT CASE WHEN (COUNT(email) > 0) THEN true ELSE false END FROM UserInfoModel userInfoModel WHERE userInfoModel.email= :email AND userInfoModel.passwordHash= :passwordHash")
    boolean canLogIn(String email, String passwordHash);

    //returns userInfoModel (with email and password)
    @Query("SELECT userInfoModel FROM UserInfoModel userInfoModel WHERE userInfoModel.email= :email ")
    UserInfoModel getUserInfo_Email(String email);


    //returns a userInfoModel (with userId)
    @Query("SELECT userInfoModel FROM UserInfoModel userInfoModel WHERE userInfoModel.userId= :userId ")
    UserInfoModel getUserInfo_ID (String userId);


    //returns true if user email does not already exist in the database
    @Query("SELECT CASE WHEN (COUNT(email) > 0) THEN true ELSE false END FROM UserInfoModel userinfoModel WHERE userinfoModel.email= :email ")
    boolean cannotSignup(String email);

    //updates the database
    @Modifying
    @Transactional
    @Query(value = "UPDATE UserInfoModel userInfoModel SET userInfoModel.passwordHash= :newPasswordHash, passwordSalt= :newPasswordSalt where userInfoModel.userId= :userId")
    void changePassword(String newPasswordHash, String newPasswordSalt,String userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE UserInfoModel userInfoModel SET userInfoModel.name= :name, userInfoModel.email= :email, userInfoModel.profilePhotoUrl= :photoUrl WHERE userInfoModel.userId= :userId")
    void updateProfile(String name, String email,String photoUrl, String userId);
}