package org.programmers.signalbuddyfinal.domain.admin.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.programmers.signalbuddyfinal.global.support.RestDocsFormatGenerators.commonResponseFormat;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.programmers.signalbuddyfinal.domain.admin.controller.AdminController;
import org.programmers.signalbuddyfinal.domain.admin.dto.AdminMemberResponse;
import org.programmers.signalbuddyfinal.domain.admin.dto.MemberFilterRequest;
import org.programmers.signalbuddyfinal.domain.admin.service.AdminService;
import org.programmers.signalbuddyfinal.domain.member.entity.enums.MemberRole;
import org.programmers.signalbuddyfinal.domain.member.entity.enums.MemberStatus;
import org.programmers.signalbuddyfinal.global.support.ControllerTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.org.apache.commons.lang3.ArrayUtils;

import static org.mockito.BDDMockito.*;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;

@WebMvcTest(AdminController.class)
public class AdminControllerTest extends ControllerTest {

    private final String tag = "Admin API";

    @MockitoBean
    private AdminService adminService;

    @DisplayName("회원 검색")
    @Test
    public void searchMember() throws Exception {
        final List<AdminMemberResponse> members = List.of(
            new AdminMemberResponse(1L, "user1@test.com", "User1", "KAKAO", MemberRole.USER,
                MemberStatus.ACTIVITY, LocalDateTime.of(2025, 1, 22, 0, 0, 0))
        );

        final Pageable pageable = PageRequest.of(0, 10);
        final Page<AdminMemberResponse> page = new PageImpl<>(members, pageable, members.size());
        final String content = "User1";

        given(adminService.searchMember(any(Pageable.class), eq(content))).willReturn(page);

        final ResultActions result = mockMvc.perform(
            get("/api/admins/members/{content}", content).param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("회원 검색", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag(tag)
                            .summary("회원 검색")
                            .description("회원 정보를 검색하는 API")
                            .pathParameters(
                                parameterWithName("content").description("검색어")
                            )
                            .queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                            )
                            .responseFields(
                                ArrayUtils.addAll(commonResponseFormat(),
                                    fieldWithPath("data.content[]").description("회원 목록"),
                                    fieldWithPath("data.content[].memberId").description("회원 ID"),
                                    fieldWithPath("data.content[].email").description("이메일"),
                                    fieldWithPath("data.content[].nickname").description("닉네임"),
                                    fieldWithPath("data.content[].oauthProvider").description(
                                        "OAuth 제공자"),
                                    fieldWithPath("data.content[].role").description("역할"),
                                    fieldWithPath("data.content[].status").description("상태"),
                                    fieldWithPath("data.content[].createdAt").description("가입일"),
                                    fieldWithPath("data.pageable").description("페이지 정보"),
                                    fieldWithPath("data.pageable.sort").description("정렬 정보"),
                                    fieldWithPath("data.pageable.sort.empty").description(
                                        "정렬 정보 존재 여부"),
                                    fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                    fieldWithPath("data.pageable.sort.unsorted").description(
                                        "비정렬 여부"),
                                    fieldWithPath("data.pageable.offset").description("페이지 시작 위치"),
                                    fieldWithPath("data.pageable.pageNumber").description(
                                        "현재 페이지 번호"),
                                    fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                    fieldWithPath("data.pageable.paged").description("페이징 사용 여부"),
                                    fieldWithPath("data.pageable.unpaged").description(
                                        "페이징 미사용 여부"),
                                    fieldWithPath("data.last").description("마지막 페이지 여부"),
                                    fieldWithPath("data.totalElements").description("총 요소 수"),
                                    fieldWithPath("data.totalPages").description("총 페이지 수"),
                                    fieldWithPath("data.size").description("페이지 크기"),
                                    fieldWithPath("data.number").description("현재 페이지 번호"),
                                    fieldWithPath("data.sort").description("정렬 정보"),
                                    fieldWithPath("data.sort.empty").description("정렬 정보 존재 여부"),
                                    fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                    fieldWithPath("data.sort.unsorted").description("비정렬 여부"),
                                    fieldWithPath("data.numberOfElements").description(
                                        "현재 페이지의 요소 수"),
                                    fieldWithPath("data.first").description("첫 페이지 여부"),
                                    fieldWithPath("data.empty").description("결과가 비어있는지 여부")
                                )
                            ).build()
                    )));
    }

    @DisplayName("회원 필터링 조회")
    @Test
    public void FilteredMember() throws Exception {
        final MemberFilterRequest filter = MemberFilterRequest.builder()
            .role(MemberRole.USER)
            .status(MemberStatus.ACTIVITY)
            .oAuthProvider("KAKAO")
            .startDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            .endDate(LocalDateTime.of(2025, 2, 20, 0, 0, 0))
            .ago(null)
            .build();

        final List<AdminMemberResponse> members = List.of(
            new AdminMemberResponse(1L, "user1@test.com", "User1", "kakao", MemberRole.USER,
                MemberStatus.ACTIVITY, LocalDateTime.of(2025, 1, 22, 0, 0, 0))
        );

        final Pageable pageable = PageRequest.of(0, 10);
        final Page<AdminMemberResponse> page = new PageImpl<>(members, pageable, members.size());

        given(adminService.getAllMemberWithFilter(any(Pageable.class),
            any(MemberFilterRequest.class)))
            .willReturn(page);

        final ResultActions result = mockMvc.perform(
                get("/api/admins/members/filter").param("page", "0")
                    .param("size", "10")
                    .param("role", filter.getRole().toString())
                    .param("status", filter.getStatus().toString())
                    .param("oAuthProvider", filter.getOAuthProvider())
                    .param("startDate", filter.getStartDate().toString())
                    .param("endDate", filter.getEndDate().toString())
                    .param("ago", filter.getAgo() != null ? filter.getAgo().toString() : null)
                    .header("Accept", "application/json"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("회원 필터링 조회", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag(tag)
                        .summary("회원 필터링 조회")
                        .description("회원 정보를 필터 기준으로 조회하는 API")
                        .queryParameters(
                            parameterWithName("page").description("페이지 번호"),
                            parameterWithName("size").description("페이지 크기"),
                            parameterWithName("role").description("회원 역할 (USER, ADMIN)").optional(),
                            parameterWithName("status").description("회원 상태 (ACTIVITY, WITHDRAWAL)")
                                .optional(),
                            parameterWithName("oAuthProvider").description(
                                "oAuth 제공자 (kakao, naver, google)").optional(),
                            parameterWithName("startDate").description(
                                "검색 시작일 (YYYY-MM-DD HH:mm:ss)").optional(),
                            parameterWithName("endDate").description("검색 종료일 (YYYY-MM-DD HH:mm:ss)")
                                .optional(),
                            parameterWithName("ago").description(
                                    "최근 기간 필터링 (TODAY, THREE_DAYS, WEEK, MONTH, THREE_MONTH)")
                                .optional()
                        )
                        .responseFields(
                            ArrayUtils.addAll(commonResponseFormat(),
                                fieldWithPath("data.content[]").description("회원 목록"),
                                fieldWithPath("data.content[].memberId").description("회원 ID"),
                                fieldWithPath("data.content[].email").description("이메일"),
                                fieldWithPath("data.content[].nickname").description("닉네임"),
                                fieldWithPath("data.content[].oauthProvider").description(
                                    "OAuth 제공자"),
                                fieldWithPath("data.content[].role").description("역할"),
                                fieldWithPath("data.content[].status").description("상태"),
                                fieldWithPath("data.content[].createdAt").description("가입일"),
                                fieldWithPath("data.pageable").description("페이지 정보"),
                                fieldWithPath("data.pageable.sort").description("정렬 정보"),
                                fieldWithPath("data.pageable.sort.empty").description(
                                    "정렬 정보 존재 여부"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("비정렬 여부"),
                                fieldWithPath("data.pageable.offset").description("페이지 시작 위치"),
                                fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("data.pageable.paged").description("페이징 사용 여부"),
                                fieldWithPath("data.pageable.unpaged").description("페이징 미사용 여부"),
                                fieldWithPath("data.last").description("마지막 페이지 여부"),
                                fieldWithPath("data.totalElements").description("총 요소 수"),
                                fieldWithPath("data.totalPages").description("총 페이지 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.number").description("현재 페이지 번호"),
                                fieldWithPath("data.sort").description("정렬 정보"),
                                fieldWithPath("data.sort.empty").description("정렬 정보 존재 여부"),
                                fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                fieldWithPath("data.sort.unsorted").description("비정렬 여부"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지의 요소 수"),
                                fieldWithPath("data.first").description("첫 페이지 여부"),
                                fieldWithPath("data.empty").description("결과가 비어있는지 여부")
                            )
                        ).build()
                )));
    }
}
