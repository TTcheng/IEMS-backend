package me.wcc.auth.service;

import me.wcc.auth.entity.CustomUserDetails;
import me.wcc.auth.entity.Role;
import me.wcc.auth.entity.User;
import me.wcc.auth.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * @author chuncheng.wang@hand-china.com 19-3-10 下午9:58
 */
@Service
@Transactional
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    TokenStore tokenStore;

    @Override
    public UserDetails loadUserByUsername(String s) {
        User user = userMapper.selectOne(new User(s));
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // TODO userRoles
        user.setRoles(Collections.singletonList(new Role("USER")));
        return new CustomUserDetails(user);
    }
}
