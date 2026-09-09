package fr.dev.sensei.guild.keeper.recruitment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RecruitmentService recruitmentService;

    @Test
    void should_recruit_candidate_when_name_is_valid_and_not_taken() {
        // Arrange
        when(memberRepository.findByName("Dorian")).thenReturn(Optional.empty());

        // Act
        Member recruit = recruitmentService.recruit("Dorian");

        // Assert
        assertThat(recruit.name()).isEqualTo("Dorian");
        assertThat(recruit.rank()).isEqualTo(MemberRank.NOVICE);
        assertThat(recruit.experiencePoints()).isZero();

        ArgumentCaptor<Member> savedMember = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(savedMember.capture());
        assertThat(savedMember.getValue().name()).isEqualTo("Dorian");
    }

    @Test
    void should_throw_DuplicateMemberException_when_name_already_exists() {
        //When
        Member member = new Member("0", "Dorian", MemberRank.NOVICE, 0, 5);
        when(memberRepository.findByName("Dorian")).thenReturn(Optional.of(member));

        //Act / Assert
        assertThatThrownBy(() -> recruitmentService.recruit("Dorian"))
            .isInstanceOf(DuplicateMemberException.class)
            .hasMessageContaining("Dorian");

        verify(memberRepository, never()).save(any());
    }

    @Test
    void should_reject_candidate_when_name_is_blank() {
        //Act / Assert
        assertThatThrownBy(() -> recruitmentService.recruit(" "))
            .isInstanceOf(IllegalArgumentException.class);

        verify(memberRepository, never()).findByName(any());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void should_reject_candidate_when_name_is_null() {
        //Act / Assert
        assertThatThrownBy(() -> recruitmentService.recruit(null)).isInstanceOf(IllegalArgumentException.class);

        verify(memberRepository, never()).findByName(any());
        verify(memberRepository, never()).save(any());
    }
}
