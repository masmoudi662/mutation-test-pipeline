java
package de.n26.n26androidsamples.credit.data;

import org.junit.Before;
import org.junit.Test;

import de.n26.n26androidsamples.base.data.EssentialParamMissingException;
import de.n26.n26androidsamples.credit.data.CreditDataConstants.RawDraftStatus;
import de.n26.n26androidsamples.credit.data.CreditDraft.CreditDraftStatus;
import de.n26.n26androidsamples.credit.data.CreditDraftRaw.RepaymentInfoRaw;
import polanski.option.Option;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreditDraftMapperTest {

    private CreditDraftMapper creditDraftMapper;

    @Before
    public void setUp() {
        creditDraftMapper = new CreditDraftMapper();
    }

    @Test
    public void apply_validRawCreditDraft_returnsCreditDraft() throws Exception {
        CreditDraftRaw raw = mock(CreditDraftRaw.class);
        when(raw.id()).thenReturn("123");
        when(raw.purposeName()).thenReturn("Car");
        when(raw.amount()).thenReturn(1000.0);
        when(raw.status()).thenReturn(RawDraftStatus.DRAFT);
        RepaymentInfoRaw repaymentInfoRaw = mock(RepaymentInfoRaw.class);
        when(raw.repaymentInfo()).thenReturn(Option.of(repaymentInfoRaw));
        when(raw.imageUrl()).thenReturn("http://example.com/image.jpg");
        when(raw.purposeId()).thenReturn("456");

        CreditDraft creditDraft = creditDraftMapper.apply(raw);

        assertNotNull(creditDraft);
        assertEquals("123", creditDraft.id());
        assertEquals("Car", creditDraft.purpose());
        assertEquals(1000.0, creditDraft.amount(), 0.001);
        assertEquals(CreditDraftStatus.DRAFT, creditDraft.status());
        assertEquals("http://example.com/image.jpg", creditDraft.imageUrl());
        assertEquals("456", creditDraft.purposeId());
    }

    @Test(expected = EssentialParamMissingException.class)
    public void apply_nullId_throwsException() throws Exception {
        CreditDraftRaw raw = mock(CreditDraftRaw.class);
        when(raw.id()).thenReturn(null);
        creditDraftMapper.apply(raw);
    }

    @Test(expected = EssentialParamMissingException.class)
    public void apply_nullPurposeName_throwsException() throws Exception {
        CreditDraftRaw raw = mock(CreditDraftRaw.class);
        when(raw.id()).thenReturn("123");
        when(raw.purposeName()).thenReturn(null);
        creditDraftMapper.apply(raw);
    }

    @Test(expected = EssentialParamMissingException.class)
    public void apply_nullAmount_throwsException() throws Exception {
        CreditDraftRaw raw = mock(CreditDraftRaw.class);
        when(raw.id()).thenReturn("123");
        when(raw.purposeName()).thenReturn("Car");
        when(raw.amount()).thenReturn(null);
        creditDraftMapper.apply(raw);
    }

    @Test
    public void apply_nullStatus_returnsDraftStatus() throws Exception {
        CreditDraftRaw raw = mock(CreditDraftRaw.class);
        when(raw.id()).thenReturn("123");
        when(raw.purposeName()).thenReturn("Car");
        when(raw.amount()).thenReturn(1000.0);
        when(raw.status()).thenReturn(null);
        when(raw.repaymentInfo()).thenReturn(Option.absent());
        when(raw.imageUrl()).thenReturn("http://example.com/image.jpg");
        when(raw.purposeId()).thenReturn("456");

        CreditDraft creditDraft = creditDraftMapper.apply(raw);

        assertEquals(CreditDraftStatus.DRAFT, creditDraft.status());
    }

    @Test
    public void apply_nullRepaymentInfo_returnsAbsent() throws Exception {
        CreditDraftRaw raw = mock(CreditDraftRaw.class);
        when(raw.id()).thenReturn("123");
        when(raw.purposeName()).thenReturn("Car");
        when(raw.amount()).thenReturn(1000.0);
        when(raw.status()).thenReturn(RawDraftStatus.DRAFT);
        when(raw.repaymentInfo()).thenReturn(Option.absent());
        when(raw.imageUrl()).thenReturn("http://example.com/image.jpg");
        when(raw.purposeId()).thenReturn("456");

        CreditDraft creditDraft = creditDraftMapper.apply(raw);

        assertNotNull(creditDraft);
    }

    @Test(expected = EssentialParamMissingException.class)
    public void apply_nullRaw_throwsException() throws Exception {
        creditDraftMapper.apply(null);
    }
}