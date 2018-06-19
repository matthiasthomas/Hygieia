package com.capitalone.dashboard.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.capitalone.dashboard.ApiSettings;
import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.auth.AuthenticationResponseService;
import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.auth.DefaultAuthenticationResponseService;
import com.capitalone.dashboard.auth.ldap.CustomUserDetailsContextMapper;
import com.capitalone.dashboard.auth.standard.StandardAuthenticationProvider;
import com.capitalone.dashboard.auth.token.JwtAuthenticationFilter;
import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.capitalone.dashboard.service.AggregateService;
import com.capitalone.dashboard.service.ApiTokenService;
import com.capitalone.dashboard.service.AuthenticationService;
import com.capitalone.dashboard.service.BinaryArtifactService;
import com.capitalone.dashboard.service.BuildService;
import com.capitalone.dashboard.service.BusCompOwnerService;
import com.capitalone.dashboard.service.CloudInstanceService;
import com.capitalone.dashboard.service.CloudSubnetService;
import com.capitalone.dashboard.service.CloudVirtualNetworkService;
import com.capitalone.dashboard.service.CloudVolumeService;
import com.capitalone.dashboard.service.CmdbService;
import com.capitalone.dashboard.service.CodeQualityService;
import com.capitalone.dashboard.service.CollectorService;
import com.capitalone.dashboard.service.CommitService;
import com.capitalone.dashboard.service.DashboardRemoteService;
import com.capitalone.dashboard.service.DashboardService;
import com.capitalone.dashboard.service.DeployService;
import com.capitalone.dashboard.service.EncryptionService;
import com.capitalone.dashboard.service.ErrorLogService;
import com.capitalone.dashboard.service.FeatureService;
import com.capitalone.dashboard.service.FunctionalTestService;
import com.capitalone.dashboard.service.GitKPIService;
import com.capitalone.dashboard.service.GitRepoDetailsService;
import com.capitalone.dashboard.service.GitRequestService;
import com.capitalone.dashboard.service.IntegrationTestService;
import com.capitalone.dashboard.service.LibraryPolicyService;
import com.capitalone.dashboard.service.MaturityModelService;
import com.capitalone.dashboard.service.Monitor2Service;
import com.capitalone.dashboard.service.PerformanceService;
import com.capitalone.dashboard.service.PipelineService;
import com.capitalone.dashboard.service.ReleaseService;
import com.capitalone.dashboard.service.RoleService;
import com.capitalone.dashboard.service.ScopeService;
import com.capitalone.dashboard.service.ServiceNowService;
import com.capitalone.dashboard.service.ServiceService;
import com.capitalone.dashboard.service.TeamService;
import com.capitalone.dashboard.service.TemplateService;
import com.capitalone.dashboard.service.TestResultService;
import com.capitalone.dashboard.service.UnitTestService;
import com.capitalone.dashboard.service.UserInfoService;
import com.capitalone.dashboard.util.PaginationHeaderUtility;

/**
 * Spring context configuration for Testing purposes
 */
@Configuration
public class TestConfig {

	@Bean
	public ReleaseService releaseService() {
		return Mockito.mock(ReleaseService.class);
	}
	
	@Bean
	public AuthenticationService authenticationService() {
		return Mockito.mock(AuthenticationService.class);
	}

	@Bean
	public AuthenticationResponseService authenticationResponseService() {
		return Mockito.mock(AuthenticationResponseService.class);
	}

	@Bean
	public DashboardService dashboardService() {
		return Mockito.mock(DashboardService.class);
	}

	@Bean
	public AggregateService aggregateService() {
		return Mockito.mock(AggregateService.class);
	}

	@Bean
	public RoleService roleService() {
		return Mockito.mock(RoleService.class);
	}

	@Bean
	public ApiSettings apiSettingsService() {
		ApiSettings mockito = Mockito.mock(ApiSettings.class);

		mockito.setCorsEnabled(false);
		return Mockito.mock(ApiSettings.class);
	}

	@Bean
	public BuildService buildService() {
		return Mockito.mock(BuildService.class);
	}

	@Bean
	public CollectorService collectorService() {
		return Mockito.mock(CollectorService.class);
	}

	@Bean
	public ServiceService serviceService() {
		return Mockito.mock(ServiceService.class);
	}

	@Bean
	public DeployService deployService() {
		return Mockito.mock(DeployService.class);
	}

	@Bean
	public CloudSubnetService cloudService() {
		return Mockito.mock(CloudSubnetService.class);
	}

	@Bean
	public CodeQualityService codeQualityService() {
		return Mockito.mock(CodeQualityService.class);
	}

	@Bean
	public CommitService commitService() {
		return Mockito.mock(CommitService.class);
	}

	@Bean
	public TestResultService testResultService() {
		return Mockito.mock(TestResultService.class);
	}

	@Bean
	public FeatureService featureService() {
		return Mockito.mock(FeatureService.class);
	}

	@Bean
	public ScopeService scopeService() {
		return Mockito.mock(ScopeService.class);
	}

	@Bean
	public EncryptionService encryptionService() {
		return Mockito.mock(EncryptionService.class);
	}

	@Bean
	public BinaryArtifactService artifactService() {
		return Mockito.mock(BinaryArtifactService.class);
	}

	@Bean
	public PipelineService pipelineService() {
		return Mockito.mock(PipelineService.class);
	}

	@Bean
	public CloudInstanceService cloudInstanceService() {
		return Mockito.mock(CloudInstanceService.class);
	}

	@Bean
	public CloudSubnetService cloudSubnetService() {
		return Mockito.mock(CloudSubnetService.class);
	}

	@Bean
	public CloudVirtualNetworkService cloudVirtualNetworkService() {
		return Mockito.mock(CloudVirtualNetworkService.class);
	}

	@Bean
	public CloudVolumeService cloudVolumeService() {
		return Mockito.mock(CloudVolumeService.class);
	}

	@Bean
	public ServiceNowService serviceNowService() {
		return Mockito.mock(ServiceNowService.class);
	}

	@Bean
	public PaginationHeaderUtility paginationHeaderUtility() {

		return Mockito.mock(PaginationHeaderUtility.class);
	}

	@Bean
	public TeamService teamService() {
		return Mockito.mock(TeamService.class);
	}

	@Bean
	public GitRequestService gitRequestService() {
		return Mockito.mock(GitRequestService.class);
	}

	@Bean
	public LibraryPolicyService libraryPolicyService() {
		return Mockito.mock(LibraryPolicyService.class);
	}

	@Bean
	public MaturityModelService maturityModelService() {
		return Mockito.mock(MaturityModelService.class);
	}

	@Bean
	public PerformanceService performanceService() {
		return Mockito.mock(PerformanceService.class);
	}

	@Bean
	public GitKPIService gitKPIService() {
		return Mockito.mock(GitKPIService.class);
	}

	@Bean
	public GitRepoDetailsService gitRepoDetailsService() {
		return Mockito.mock(GitRepoDetailsService.class);
	}

	@Bean
	public IntegrationTestService integrationTestService() {
		return Mockito.mock(IntegrationTestService.class);
	}

	@Bean
	public ErrorLogService errorLogService() {
		return Mockito.mock(ErrorLogService.class);
	}

	@Bean
	public Monitor2Service monitor2Service() {
		return Mockito.mock(Monitor2Service.class);
	}

	@Bean
	public AuthProperties authProperties() {
		return Mockito.mock(AuthProperties.class);
	}

	@Bean
	public UserInfoService userInfoService() {
		return Mockito.mock(UserInfoService.class);
	}

	@Bean
	public ApiTokenService apiTokenService() {
		return Mockito.mock(ApiTokenService.class);
	}

	@Bean
	public CmdbService cmdbService() {
		return Mockito.mock(CmdbService.class);
	}

	@Bean
	public BusCompOwnerService busCompOwnerService() {
		return Mockito.mock(BusCompOwnerService.class);
	}

	@Bean
	public DashboardRemoteService dashboardRemoteService() {
		return Mockito.mock(DashboardRemoteService.class);
	}

	@Bean
	public TemplateService templateService() {
		return Mockito.mock(TemplateService.class);
	}

	@Bean
	public FunctionalTestService functionalTestService() {
		return Mockito.mock(FunctionalTestService.class);
	}

	@Bean
	public PropertyPlaceholderConfigurer propConfig() {
		PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
		placeholderConfigurer.setLocation(new ClassPathResource(
				"test.properties"));
		return placeholderConfigurer;
	}

	@Bean
	public AuthenticationResultHandler authenticationResultHandler() {
		return Mockito.mock(AuthenticationResultHandler.class);
	}

	@Bean
	public DefaultAuthenticationResponseService defaultAuthenticationResponseService() {
		return Mockito.mock(DefaultAuthenticationResponseService.class);
	}

	@Bean
	public CustomUserDetailsContextMapper customUserDetailsContextMapper() {
		return Mockito.mock(CustomUserDetailsContextMapper.class);
	}

	@Bean
	public StandardAuthenticationProvider standardAuthenticationProvider() {
		return Mockito.mock(StandardAuthenticationProvider.class);
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return Mockito.mock(JwtAuthenticationFilter.class);
	}

	@Bean
	public TokenAuthenticationService tokenAuthenticationService() {
		return Mockito.mock(TokenAuthenticationService.class);
	}
	
	@Bean
	public UnitTestService unitTestService() {
		return Mockito.mock(UnitTestService.class);
	}

}