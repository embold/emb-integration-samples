/**
 * @interface IVisitor
 * Visitor interface for double-dispatch operations on animal types.
 */
class IVisitor {
    /** @param {import('./Dog')} dog */ visitDog(dog) { throw new Error('Not implemented'); }
    /** @param {import('./Cat')} cat */ visitCat(cat) { throw new Error('Not implemented'); }
}

module.exports = IVisitor;
