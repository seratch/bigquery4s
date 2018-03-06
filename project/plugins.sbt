addSbtPlugin("org.scalariform"  % "sbt-scalariform"      % "1.8.2")
addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"         % "2.0")
addSbtPlugin("com.jsuereth"     % "sbt-pgp"              % "1.1.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"          % "0.3.3")
//addSbtPlugin("org.scoverage"    % "sbt-scoverage"        % "1.3.5")
//addSbtPlugin("org.scoverage"    % "sbt-coveralls"        % "1.0.0")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
