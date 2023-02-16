package org.enzopapiro.cache;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.AtomicBuffer;

public class BidAskFlyweight implements RecordSizeProvider {
    private static final int ID_OFFSET = 0;
    private static final int SCALE_OFFSET = ID_OFFSET + Long.BYTES;
    private static final int BID_OFFSET = SCALE_OFFSET + Integer.BYTES;
    private static final int ASK_OFFSET = BID_OFFSET + Long.BYTES;
    private static final int PUP_TS_OFFSET = ASK_OFFSET + Long.BYTES;
    private static final int PTX_TS_OFFSET = PUP_TS_OFFSET + Long.BYTES;
    private static final int RFQ_TS_OFFSET = PTX_TS_OFFSET + Long.BYTES;
    private static final int RFQTX_TS_OFFSET = RFQ_TS_OFFSET + Long.BYTES;
    private static final int RECORD_LENGTH = RFQTX_TS_OFFSET + Long.BYTES;

    private AtomicBuffer buffer;
    private int id_offset;
    private int scale_offset;
    private int bid_offset;
    private int ask_offset;
    private int pup_ts_offset;
    private int ptx_ts_offset;
    private int rfq_ts_offset;
    private int rfqtx_ts_offset;

    public BidAskFlyweight wrap(AtomicBuffer buffer,int offset) {
        this.buffer = buffer;
        this.id_offset = offset;
        this.scale_offset = this.id_offset + SCALE_OFFSET;
        this.bid_offset = this.scale_offset + BID_OFFSET;
        this.ask_offset = this.bid_offset + ASK_OFFSET;
        this.pup_ts_offset = this.ask_offset + PUP_TS_OFFSET;
        this.ptx_ts_offset = this.pup_ts_offset + PTX_TS_OFFSET;
        this.rfq_ts_offset = this.ptx_ts_offset + RFQ_TS_OFFSET;
        this.rfqtx_ts_offset = this.rfq_ts_offset + RFQTX_TS_OFFSET;
        return this;
    }

    public void setId(long id) {
        buffer.putLong(ID_OFFSET, id);
    }

    public long getId() {
        return buffer.getLong(ID_OFFSET);
    }

    public void setScale(int scale) {
        buffer.putInt(SCALE_OFFSET, scale);
    }

    public int getScale() {
        return buffer.getInt(SCALE_OFFSET);
    }

    public void setBid(long bid) {
        buffer.putLong(BID_OFFSET, bid);
    }

    public long getBid() {
        return buffer.getLong(BID_OFFSET);
    }

    public void setAsk(long ask) {
        buffer.putLong(ASK_OFFSET, ask);
    }

    public long getAsk() {
        return buffer.getLong(ASK_OFFSET);
    }

    public void setPupTs(long pupTs) {
        buffer.putLong(PUP_TS_OFFSET, pupTs);
    }

    public long getPupTs() {
        return buffer.getLong(PUP_TS_OFFSET);
    }

    public void setPtxTs(long ptxTs) {
        buffer.putLong(PTX_TS_OFFSET, ptxTs);
    }

    public long getPtxTs() {
        return buffer.getLong(PTX_TS_OFFSET);
    }

    public void setRfqTs(long rfqTs) {
        buffer.putLong(RFQ_TS_OFFSET, rfqTs);
    }

    public long getRfqTs() {
        return buffer.getLong(RFQ_TS_OFFSET);
    }

    public void setRfqtxTs(long rfqtxTs) {
        buffer.putLong(RFQTX_TS_OFFSET, rfqtxTs);
    }

    public long getRfqtxTs() {
        return buffer.getLong(RFQTX_TS_OFFSET);
    }

    public static int getRecordLength() {
        return RECORD_LENGTH;
    }

    public void decode(DirectBuffer directBuffer, int index) {
        buffer.putBytes(0, directBuffer, index, RECORD_LENGTH);
    }

    @Override
    public int getRecordSize() {
        return RECORD_LENGTH;
    }
}
