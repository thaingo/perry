FROM cwds/javajdk

# Performance tests
RUN yum -y install wget
RUN cd /opt/ \
    && wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-4.0.tgz \
    && tar -xvzf apache-jmeter-4.0.tgz \
    && rm apache-jmeter-4.0.tgz
ENV JMETER_HOME /opt/apache-jmeter-4.0/
RUN mkdir /opt/cap-perf-tests
ADD jmeter /opt/cap-perf-tests
ENV JMETER_TESTS /opt/cap-perf-tests
RUN mkdir $JMETER_TESTS/results
ENV JM_RESULTS_DIR $JMETER_TESTS/results
RUN mkdir $JMETER_TESTS/data
ADD entrypoint.sh $JMETER_TESTS
RUN chmod +x $JMETER_TESTS/entrypoint.sh
WORKDIR $JMETER_TESTS
CMD ["/opt/cap-perf-tests/entrypoint.sh"]
