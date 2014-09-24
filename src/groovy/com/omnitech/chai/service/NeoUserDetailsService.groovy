package com.omnitech.chai.service

import com.omnitech.chai.repositories.UserRepository
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.data.neo4j.transaction.Neo4jTransactional
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Neo4jTransactional
class NeoUserDetailsService implements GrailsUserDetailsService {

    /**
     * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least one role, so
     * we give a user with no granted roles this one which gets past that restriction but
     * doesn't grant anything.
     */
    static final GrantedAuthority NO_ROLE = new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)

    @Autowired
    UserRepository userRepository

    @Override
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException, DataAccessException {
        def user = userRepository.findByUsername(username)

        if (!user) throw new UsernameNotFoundException("User not found [$username]")

        def authorities = user.roles.collect { new SimpleGrantedAuthority(it.authority) }

        new GrailsUser(user.username, user.password, user.enabled, !user.accountExpired, !user.passwordExpired, !user.accountLocked, authorities, user.id)
    }

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return loadUserByUsername(s, true)
    }
}
