// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'security.UserRole'
grails.plugin.springsecurity.authority.className = 'security.Role'
grails.plugin.springsecurity.rest.token.storage.jwt.secret = '9e548c06c9bc1fae358e0bb166097c47cafc9e8be05bca289409ec77e0836964'
grails.plugin.springsecurity.rest.token.storage.jwt.expiration = 3600
grails.plugin.springsecurity.password.algorithm = 'bcrypt'
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
grails.plugin.springsecurity.securityConfigType = "Annotation"
grails.plugin.springsecurity.filterChain.chainMap = [
		'/api/**': 'JOINED_FILTERS,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter',  // Stateless chain
		'/**': 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'                                                                          // Traditional chain
]


