package com.food.ordering.zinger.dao;

import com.food.ordering.zinger.constant.Column.UserColumn;
import com.food.ordering.zinger.constant.ErrorLog;
import com.food.ordering.zinger.constant.Query.UserQuery;
import com.food.ordering.zinger.model.RequestHeaderModel;
import com.food.ordering.zinger.model.Response;
import com.food.ordering.zinger.model.UserModel;
import com.food.ordering.zinger.rowMapperLambda.UserRowMapperLambda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UtilsDao {

    @Autowired
    Environment env;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Response<UserModel> validateUser(RequestHeaderModel requestHeaderModel) {
        UserModel userModel = null;
        Response<UserModel> response = new Response<>();

        try {

            if(requestHeaderModel.getOauthId().equals(env.getProperty("sa_auth")) && requestHeaderModel.getMobile().equals(env.getProperty("sa_mobile")) && requestHeaderModel.getRole().equals(env.getProperty("sa_role")))
            {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                return response;
            }

            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(UserColumn.oauthId, requestHeaderModel.getOauthId())
                    .addValue(UserColumn.mobile, requestHeaderModel.getMobile())
                    .addValue(UserColumn.role, requestHeaderModel.getRole());

            try {
                userModel = namedParameterJdbcTemplate.queryForObject(UserQuery.validateUser, parameters, UserRowMapperLambda.userRowMapperLambda);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (userModel != null) {
                response.setCode(ErrorLog.CodeSuccess);
                response.setMessage(ErrorLog.Success);
                response.setData(userModel);
            }
        }
        return response;
    }
}
