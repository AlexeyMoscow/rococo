package guru.qa.rococo.data

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UuidGenerator
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var username: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    var enabled: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    var authorities: MutableList<AuthorityEntity> = mutableListOf()
)
