package com.payneteasy.strilog.sender.offset;

import java.util.Optional;

public interface ICommitOffsetRepository {

    void saveCommitOffset(CommitOffset aCommitOffset);

    Optional<CommitOffset> getCommitOffset(String aPath);

}
